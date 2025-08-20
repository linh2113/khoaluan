import os

from fastapi import FastAPI
from typing import List, Dict, Tuple, Any
import joblib, pandas as pd
from collections import defaultdict
from mysql.connector import pooling, Error
import logging
import sys
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)s | %(message)s",
    stream=sys.stdout
)

def log(msg, **kwargs):
    logging.info(msg + " | " + " ".join(f"{k}={v}" for k, v in kwargs.items()))

DB_CFG = dict(
    host=os.getenv("DB_HOST","mysql"),
    user=os.getenv("DB_USER","root"),
    password=os.getenv("DB_PASSWORD",""),
    database=os.getenv("DB_NAME","techstorefinal"),
    autocommit=os.getenv("DB_AUTOCOMMIT","true").lower()=="true",
    charset="utf8mb4"
)

POOL = pooling.MySQLConnectionPool(
    pool_name="rec_pool",
    pool_size=10,
    **DB_CFG
)
MAX_K = 30

SQL = """
SELECT p.product_id_string, r.rating
FROM   rating r
JOIN   products p ON p.id = r.id_product
WHERE  r.id_user = %s   
ORDER BY r.create_at DESC
LIMIT 5
"""

def fetch_user_purchases(user_id: str) -> List[str]:
    try:
        conn = POOL.get_connection()
        with conn.cursor() as cur:
            cur.execute("""
                SELECT DISTINCT p.product_id_string
                FROM orders o
                JOIN order_details od ON o.id = od.id_order
                JOIN products p ON od.id_product = p.id
                WHERE o.id_user = %s
                  AND od.review_status = 0
            """, (int(user_id),))
            rows = cur.fetchall()
            products = [row[0] for row in rows]
            log("fetch_user_purchases", user=user_id, count=len(products), products=products)
            return products
    except Error as e:
        logging.error("MySQL error (purchase): %s", e)
        return []
    finally:
        if conn: conn.close()

ITEM_CF = joblib.load("./best_knn_model.joblib")
STATS   = pd.read_parquet("./item_stats.parquet")

# Map giữa inner id <-> raw id dùng bởi Surprise

IDX2RAW_CF = {i: ITEM_CF.trainset.to_raw_iid(i)
              for i in ITEM_CF.trainset.all_items()}
RAW2IDX_CF = {v: k for k, v in IDX2RAW_CF.items()}
#Tính GLobal_mean
GLOBAL_MEAN = ITEM_CF.trainset.global_mean
app = FastAPI()

def popular_products(k: int) -> List[str]:
    top_items = (STATS.sort_values(["mean_rating", "rating_count"], ascending=[False, False])
                      .head(k)["item_id"].astype(str).tolist())
    log("popular_products", top_k=top_items)
    return top_items
#Chỉ lấy số 5 số rating gần nhất
def fetch_latest_user_ratings(user_id: str, limit: int = 5) -> List[Tuple[str, float]]:
    try:
        conn = POOL.get_connection()
        with conn.cursor() as cur:
            cur.execute(SQL, (int(user_id),))
            rows = cur.fetchall()
            log("fetch_user_ratings", user=user_id, count=len(rows), ratings=rows)
            return rows
    except Error as e:
        logging.error("MySQL error:", e)
        return []
    finally:
        if conn: conn.close()

def to_inner_uid_optional(raw_uid: str) -> Tuple[bool, int]:
    ts = ITEM_CF.trainset
    if raw_uid in ts._raw2inner_id_users:
        return True, ts.to_inner_uid(raw_uid)
    return False, -1

def to_inner_iid_optional(raw_iid: str) -> Tuple[bool, int]:
    ts = ITEM_CF.trainset
    if raw_iid in ts._raw2inner_id_items:
        return True, ts.to_inner_iid(raw_iid)
    return False, -1

def get_bu(inner_uid: int) -> float:
    # User mới không có inner id hợp lệ => b_u = 0
    if inner_uid < 0:
        return 0.0
    try:
        return float(ITEM_CF.bu[inner_uid])
    except Exception:
        return 0.0

def get_bi(inner_iid: int) -> float:
    if inner_iid < 0:
        return 0.0
    try:
        return float(ITEM_CF.bi[inner_iid])
    except Exception:
        return 0.0

def baseline_ui(inner_uid: int, inner_iid: int) -> Dict[str, float]:
    mu = float(GLOBAL_MEAN)
    bu = get_bu(inner_uid)
    bi = get_bi(inner_iid)
    return {"mu": mu, "bu": bu, "bi": bi, "b_ui": mu + bu + bi}

def explain_predictions_for_user(
    raw_uid: str,
    rated_pairs: List[Tuple[int, float]],   # (inner_iid, r_ui) các item user đã rate
    top_k_neighbors_per_i: int = 100,
    top_k_return: int = 8
) -> Tuple[List[str], Dict[str, Any]]:
    """
    Dự đoán đúng chuẩn KNNBaseline cho user raw_uid.
    Trả về (danh sách gợi ý raw_iid, giải thích chi tiết).
    """
    ts = ITEM_CF.trainset
    user_known, inner_uid = to_inner_uid_optional(raw_uid)

    # Tập item user đã rate (inner id)
    rated_dict = dict(rated_pairs)

    # Bộ tích luỹ tử số / mẫu số cho từng ứng viên j
    numerators: Dict[int, float] = defaultdict(float)
    denominators: Dict[int, float] = defaultdict(float)
    # Lưu giải thích theo ứng viên
    details: Dict[int, Dict[str, Any]] = {}

    for i_cf, r_ui in rated_pairs:
        # baseline cho (u,i), residual
        b_i = baseline_ui(inner_uid, i_cf)
        resid = r_ui - b_i["b_ui"]

        neighbors = ITEM_CF.get_neighbors(i_cf, k=top_k_neighbors_per_i)
        for j_cf in neighbors:
            if j_cf in rated_dict:
                continue  # bỏ item đã rate
            sim_ij = float(ITEM_CF.sim[i_cf, j_cf])
            numerators[j_cf] += sim_ij * resid
            denominators[j_cf] += abs(sim_ij)

            # Lưu lại hạng tử đóng góp để in ra
            d = details.setdefault(j_cf, {
                "candidate_raw_iid": IDX2RAW_CF[j_cf],
                "baseline_uj": None,       # sẽ điền sau
                "terms": [],               # các hạng tử từ từng i
                "numerator": 0.0,
                "denominator": 0.0,
                "prediction": None
            })
            d["terms"].append({
                "from_item_raw_iid": IDX2RAW_CF[i_cf],
                "sim_ij": sim_ij,
                "r_ui": r_ui,
                "baseline_ui": b_i,            # mu, bu, bi, b_ui
                "residual_r_ui": resid,
                "contrib": sim_ij * resid
            })

    # Tính \hat r_{uj} = b_uj + num/den
    predictions: List[Tuple[int, float]] = []
    for j_cf, num in numerators.items():
        den = denominators[j_cf]
        b_j = baseline_ui(inner_uid, j_cf)  # baseline cho (u,j)
        if den > 0:
            est = b_j["b_ui"] + (num / den)
        else:
            est = b_j["b_ui"]  # không có hàng xóm hợp lệ

        predictions.append((j_cf, est))

        d = details[j_cf]
        d["baseline_uj"] = b_j
        d["numerator"] = num
        d["denominator"] = den
        d["prediction"] = est

        # Ghi log rành mạch
        log("explain_candidate",
            user=raw_uid,
            cand_raw_iid=IDX2RAW_CF[j_cf],
            mu=b_j["mu"], bu=b_j["bu"], bi=b_j["bi"],
            baseline_uj=b_j["b_ui"],
            numerator=num, denominator=den, est=est)

    # Sắp xếp theo dự đoán giảm dần
    predictions.sort(key=lambda x: x[1], reverse=True)
    top = predictions[:top_k_return]
    result_raw = [IDX2RAW_CF[j] for j, _ in top]

    return result_raw, {
        "user_raw_uid": raw_uid,
        "user_inner_uid": inner_uid if user_known else None,
        "global_mean": GLOBAL_MEAN,
        "candidates_explained": [details[j] for j, _ in top]
    }
def prepare_user_rated_from_model_or_db(raw_uid: str) -> List[Tuple[int, float]]:
    """
    Trả về danh sách (inner_iid, r_ui) dùng để suy luận.
    - Nếu user có trong trainset => lấy từ ts.ur
    - Nếu không => dùng 5 rating gần nhất trong DB (nếu map được vào model)
    """
    ts = ITEM_CF.trainset
    user_known, inner_uid = to_inner_uid_optional(raw_uid)
    if user_known:
        rated = [(i, r) for (i, r) in ts.ur[inner_uid]]  # r là float
        log("user_in_model", user=raw_uid, rated_count=len(rated))
        return rated

    rated_db = fetch_latest_user_ratings(raw_uid)
    rated: List[Tuple[int, float]] = []
    for raw_iid, r in rated_db:
        ok, i_cf = to_inner_iid_optional(raw_iid)
        if ok:
            rated.append((i_cf, float(r)))
        else:
            log("rating_not_in_model", product=raw_iid)
    if rated:
        log("user_from_db_ratings", user=raw_uid, rated_count=len(rated))
    return rated

def item_based_knn_baseline_predict(raw_uid: str, k: int) -> List[str]:
    """
    Dự đoán theo đúng KNNBaseline (item-based, Pearson baseline).
    Có fallback theo code gốc nếu thiếu dữ liệu.
    """
    rated_pairs = prepare_user_rated_from_model_or_db(raw_uid)

    if not rated_pairs:
        # Không có rating => thử purchase -> fake rating 3.5
        purchases = fetch_user_purchases(raw_uid)
        if purchases:
            rated_pairs = []
            for raw_iid in purchases:
                ok, i_cf = to_inner_iid_optional(raw_iid)
                if ok:
                    rated_pairs.append((i_cf, 3.5))
                else:
                    log("purchase_not_in_model", product=raw_iid)
            log("use_fake_ratings", user=raw_uid, count=len(rated_pairs))
        else:
            log("no_data_found", user=raw_uid)
            return popular_products(k)

    recs, _ = explain_predictions_for_user(
        raw_uid,
        rated_pairs,
        top_k_neighbors_per_i=100,
        top_k_return=k
    )
    return recs

# ================== CÁC ENDPOINT ========================

@app.get("/recommendations/{user_id}", response_model=List[str])
def recommend(user_id: str, k: int = 8) -> List[str]:
    k = max(1, min(k, MAX_K))
    log("recommend_start", user=user_id, k=k)
    rec = item_based_knn_baseline_predict(user_id, k)

    # Bảo đảm đủ k (bổ sung bằng popular nếu thiếu)
    if len(rec) < k:
        extras = [x for x in popular_products(k*2) if x not in rec]
        rec.extend(extras[:k - len(rec)])
        log("add_popular_to_fill", user=user_id, added=extras[:k - len(rec)])

    log("recommend_final", user=user_id, recommended=rec)
    return rec

# Endpoint mới để XEM CHI TIẾT giải thích (bu/bi/mu/num/den/terms)
@app.get("/recommendations_explain/{user_id}")
def recommend_explain(user_id: str, k: int = 8) -> Dict[str, Any]:
    k = max(1, min(k, MAX_K))
    rated_pairs = prepare_user_rated_from_model_or_db(user_id)

    if not rated_pairs:
        purchases = fetch_user_purchases(user_id)
        if purchases:
            rated_pairs = []
            for raw_iid in purchases:
                ok, i_cf = to_inner_iid_optional(raw_iid)
                if ok:
                    rated_pairs.append((i_cf, 3.5))
        else:
            # Không có gì => trả về popular + meta
            return {
                "user": user_id,
                "global_mean": GLOBAL_MEAN,
                "explainable": False,
                "reason": "No ratings or purchases found; returning popular fallback.",
                "recommendations": popular_products(k)
            }

    recs, expl = explain_predictions_for_user(
        user_id,
        rated_pairs,
        top_k_neighbors_per_i=100,
        top_k_return=k
    )

    # Thêm cả các item user đã rate (để đối chiếu)
    rated_verbose = []
    user_known, inner_uid = to_inner_uid_optional(user_id)
    for i_cf, r_ui in rated_pairs:
        b_i = baseline_ui(inner_uid if user_known else -1, i_cf)
        rated_verbose.append({
            "raw_iid": IDX2RAW_CF[i_cf],
            "inner_iid": i_cf,
            "r_ui": r_ui,
            "baseline_ui": b_i,
            "residual": r_ui - b_i["b_ui"]
        })

    return {
        "user": user_id,
        "global_mean": GLOBAL_MEAN,
        "rated_items": rated_verbose,
        "recommendations": recs,
        "explanations": expl["candidates_explained"]
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app:app", host="127.0.0.1", port=8001, reload=False, log_level="info")