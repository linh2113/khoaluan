# 🚀 Tech Store - E-Commerce Platform with AI Recommendations


[![Java](https://img.shields.io/badge/Java-21-red?style=flat-square)](https://spring.io/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-green?style=flat-square)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-15.4-black?style=flat-square)](https://nextjs.org/)

Hệ thống e-commerce bán sản phẩm công nghệ tích hợp AI gợi ý sản phẩm thông minh dựa trên hành vi người dùng.

---

## 📝 Tổng Quan Dự Án

* **Mục tiêu:** Xây dựng website bán hàng công nghệ trực tuyến từ giao diện người dùng đến trang quản trị viên, và sử dụng thuật toán Collaborative Filtering.
* **Công nghệ cốt lõi:**
  * **Backend:** Java 21 + Spring Boot 3.2.3 (REST API, Spring Security, JWT, OAuth2)
  * **Frontend:** Next.js 15 + TypeScript + Tailwind CSS 
  * **Database & Cache:** MySQL + Redis Caching
  * **AI Service:** Python triển khai thuật toán Lọc cộng tác (*Collaborative Filtering*)
  * **Tích hợp bên thứ ba:** Cloudinary (Lưu trữ ảnh online), VNPay (Cổng thanh toán)

### 🏗️ Kiến trúc hệ thống (System Architecture)
<p align="center">
  <img src="https://github.com/user-attachments/assets/43c7ad25-4336-4c5d-b57f-f2f194c27d06" alt="System Architecture Diagram" width="800">
  <br>
  <em>Sơ đồ kiến trúc hệ thống</em>
</p>

---

## ✨ Tính Năng Chính

### 👤 Khách hàng (User Web App)
* **Xác thực:** Đăng ký/đăng nhập tài khoản thường hoặc qua Google, Facebook, Discord.
* **Mua sắm:** Xem danh mục, thương hiệu, bộ lọc nâng cao, quản lý giỏ hàng.
* **Thanh toán:** Tích hợp thanh toán trực tuyến qua cổng VNPay.
* **AI Recommendation:** Đề xuất sản phẩm liên quan dựa trên lịch sử mua hàng của người dùng.
* **Tương tác:** Đánh giá (Rating) và bình luận (Review) sản phẩm.
* **Giao diện:** Chế độ tối (Dark Mode).

### 👨‍💼 Quản trị viên (Admin Dashboard)
* **Quản lý tổng thể:** CRUD Sản phẩm, Danh mục, Thương hiệu, Đơn hàng, Người dùng.
* **Chiến dịch:** Tạo và quản lý các sự kiện giảm giá và Flash Sale.
* **Thống kê:** Biểu đồ theo dõi doanh thu, sản phẩm bán chạy theo thời gian thực.

---

## 🛠️ Hướng Dẫn Cài Đặt

### 1️⃣ Clone dự án
```bash
git clone https://github.com/linh2113/khoaluan.git
cd khoaluan
```
### 2️⃣ Cấu hình Docker (Be, Database, Redis, FastAPI)
Bước 1: Tải và cài đặt Docker Desktop.

Bước 2: Tại thư mục gốc của đồ án, sao chép file .env.example thành file .env. Sau đó, mở file .env ra và điền các mã token bảo mật của bạn (Gmail App Password, OAuth2, Cloudinary, VNPay...).

Bước 3: Khởi động ứng dụng Docker Desktop trên máy tính.

Bước 4: Mở Terminal/Command Prompt tại thư mục gốc của đồ án và chạy lệnh:

```bash
docker compose up -d --build
```

### 3️⃣ Cấu hình Frontend (Next.js)

* Khởi chạy Frontend:
```bash
cd ../fe
npm install
npm run dev
```
Chạy tại: http://localhost:3000

## 📂 Cấu Trúc Thư Mục

```text
khoaluan/
├── be/                 # Backend (Spring Boot API)
├── docs/               # The khoaluan document
├── fe/                 # Frontend (Next.js Web App)
├── APIRecommend/       # API gợi ý sản phẩm bằng AI (Python)
├── mysql-init/         # SQL dump khởi tạo database ban đầu
├── .env.example        # File mẫu cấu hình các biến môi trường,
├── .gitignore          # git ignore
└── docker-compose.yml  # Khởi chạy các service bằng Docker
```
