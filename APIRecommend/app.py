from fastapi import FastAPI
from typing import List, Dict, Tuple
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
    host="localhost",
    user="root",
    password="son1234567890",
    database="techstorefinal",
    autocommit=True,
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

ITEM_CF = joblib.load("D:/Downloads/datasp/best_knn_model.joblib")
STATS   = pd.read_parquet("D:/Downloads/datasp/item_stats.parquet")

IDX2RAW_CF = {i: ITEM_CF.trainset.to_raw_iid(i)
              for i in ITEM_CF.trainset.all_items()}
RAW2IDX_CF = {v: k for k, v in IDX2RAW_CF.items()}

app = FastAPI()

def popular_products(k: int) -> List[str]:
    top_items = (STATS.sort_values(["mean_rating", "rating_count"], ascending=[False, False])
                      .head(k)["item_id"].astype(str).tolist())
    log("popular_products", top_k=top_items)
    return top_items

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

def item_based(user_id: str, k: int) -> List[str]:
    ts_cf = ITEM_CF.trainset
    if user_id in ts_cf._raw2inner_id_users:
        inner_uid = ts_cf.to_inner_uid(user_id)
        rated     = ts_cf.ur[inner_uid]
        log("user_in_model", user=user_id, rated_count=len(rated))
    else:
        rated_db = fetch_latest_user_ratings(user_id)
        rated = []
        for asin, r in rated_db:
            if asin in RAW2IDX_CF:
                rated.append((RAW2IDX_CF[asin], r))
            else:
                log("rating_not_in_model", product=asin)
        if not rated:
            return popular_products(k)

    scores: Dict[int, float] = defaultdict(float)
    rated_dict = dict(rated)
    for iid_cf, r in rated:
        neighbors = ITEM_CF.get_neighbors(iid_cf, k=100)
        for j_cf in neighbors:
            if j_cf not in rated_dict:
                sim = ITEM_CF.sim[iid_cf, j_cf]
                scores[j_cf] += sim * r

    topk = sorted(scores.items(), key=lambda x: x[1], reverse=True)[:k]
    result = [IDX2RAW_CF[i] for i, _ in topk]
    log("item_based_result", user=user_id, recommended=result)
    return result

def item_based_with_custom_ratings(user_id: str, k: int, ratings: List[Tuple[str, float]]) -> List[str]:
    rated = []
    for iid, r in ratings:
        if iid in RAW2IDX_CF:
            rated.append((RAW2IDX_CF[iid], r))
        else:
            log("purchase_not_in_model", product=iid)

    if not rated:
        return popular_products(k)

    scores: Dict[int, float] = defaultdict(float)
    rated_dict = dict(rated)
    for iid_cf, r in rated:
        neighbors = ITEM_CF.get_neighbors(iid_cf, k=100)
        for j_cf in neighbors:
            if j_cf not in rated_dict:
                sim = ITEM_CF.sim[iid_cf, j_cf]
                scores[j_cf] += sim * r

    topk = sorted(scores.items(), key=lambda x: x[1], reverse=True)[:k]
    result = [IDX2RAW_CF[i] for i, _ in topk]
    log("item_based_purchase_result", user=user_id, recommended=result)
    return result

@app.get("/recommendations/{user_id}", response_model=List[str])
def recommend(user_id: str, k: int = 8) -> List[str]:
    k = max(1, min(k, MAX_K))
    log("recommend_start", user=user_id, k=k)

    ratings = fetch_latest_user_ratings(user_id)
    if ratings:
        rec = item_based(user_id, k)
    else:
        purchases = fetch_user_purchases(user_id)
        if purchases:
            fake_ratings = [(product_id, 3.5) for product_id in purchases]
            log("use_fake_ratings", user=user_id, count=len(fake_ratings), fake_ratings=fake_ratings)
            rec = item_based_with_custom_ratings(user_id, k, fake_ratings)
        else:
            log("no_data_found", user=user_id)
            rec = popular_products(k)

    if len(rec) < k:
        extras = [x for x in popular_products(k) if x not in rec]
        rec.extend(extras[:k - len(rec)])
        log("add_popular_to_fill", user=user_id, added=extras[:k - len(rec)])

    log("recommend_final", user=user_id, recommended=rec)
    return rec

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app:app", host="127.0.0.1", port=8001, reload=False, log_level="info")
