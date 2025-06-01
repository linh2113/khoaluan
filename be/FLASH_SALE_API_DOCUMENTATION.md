# Flash Sale & Discount API Documentation

## Tổng quan
Tài liệu này mô tả các API cho hai hệ thống chính:
1. **Flash Sale**: Quản lý các đợt giảm giá đặc biệt trong thời gian giới hạn với số lượng sản phẩm có hạn
2. **Discount**: Quản lý các chương trình giảm giá cho sản phẩm và danh mục

---

# FLASH SALE API

## Tổng quan Flash Sale
Chức năng Flash Sale cho phép admin tạo và quản lý các đợt giảm giá đặc biệt trong thời gian giới hạn với số lượng sản phẩm có hạn.

## Entities

### FlashSale
- `id`: ID duy nhất
- `name`: Tên flash sale
- `description`: Mô tả
- `startTime`: Thời gian bắt đầu
- `endTime`: Thời gian kết thúc
- `createdAt`: Thời gian tạo
- `updatedAt`: Thời gian cập nhật

### FlashSaleItem
- `id`: ID duy nhất
- `flashSale`: Flash sale liên kết
- `product`: Sản phẩm liên kết
- `flashPrice`: Giá flash sale
- `stockLimit`: Giới hạn số lượng (null = không giới hạn)
- `soldCount`: Số lượng đã bán

## API Endpoints

### Admin Endpoints

#### 1. Tạo Flash Sale
```
POST /api/admin/flash-sales
```
**Request Body:**
```json
{
  "name": "Summer Sale 2024",
  "description": "Giảm giá sốc cho mùa hè 2024",
  "startTime": "2024-07-01T00:00:00",
  "endTime": "2024-07-07T23:59:59"
}
```

#### 2. Cập nhật Flash Sale
```
PUT /api/admin/flash-sales/{id}
```
**Request Body:**
```json
{
  "name": "Summer Sale 2024 - Updated",
  "description": "Giảm giá sốc cho mùa hè 2024 - Cập nhật",
  "startTime": "2024-07-01T00:00:00",
  "endTime": "2024-07-10T23:59:59"
}
```

#### 3. Xóa Flash Sale
```
DELETE /api/admin/flash-sales/{id}
```

#### 4. Lấy tất cả Flash Sales (Admin)
```
GET /api/admin/flash-sales?search={searchTerm}&page={page}&size={size}&sortBy={sortBy}&sortDir={sortDir}
```

#### 5. Thêm sản phẩm vào Flash Sale
```
POST /api/admin/flash-sales/{flashSaleId}/products
```
**Request Body:**
```json
{
  "productId": 1,
  "flashPrice": 25990000,
  "stockLimit": 50
}
```

#### 6. Cập nhật sản phẩm trong Flash Sale
```
PUT /api/admin/flash-sales/items/{flashSaleItemId}
```
**Request Body:**
```json
{
  "flashPrice": 24990000,
  "stockLimit": 60
}
```

#### 7. Xóa sản phẩm khỏi Flash Sale
```
DELETE /api/admin/flash-sales/items/{flashSaleItemId}
```

#### 8. Lấy danh sách Flash Sale Items
```
GET /api/admin/flash-sales/{flashSaleId}/items
```

#### 9. Tìm kiếm Flash Sale theo tên
```
GET /api/admin/flash-sales/search?name={name}
```

#### 10. Thống kê số lượng Flash Sale đang hoạt động
```
GET /api/admin/flash-sales/stats/active-count
```

### User Endpoints (Public)

#### 1. Lấy Flash Sales đang hoạt động
```
GET /api/flash-sales/current
```

#### 2. Lấy Flash Sales sắp tới
```
GET /api/flash-sales/upcoming
```

#### 3. Lấy Flash Sales đã kết thúc
```
GET /api/flash-sales/past
```

#### 4. Lấy chi tiết Flash Sale
```
GET /api/flash-sales/{id}
```

#### 5. Lấy sản phẩm trong Flash Sale
```
GET /api/flash-sales/{id}/products
```

#### 6. Kiểm tra sản phẩm có trong Flash Sale không
```
GET /api/flash-sales/check-product/{productId}
```

## Business Rules

### 1. Validation Rules
- Thời gian bắt đầu phải sau thời gian hiện tại
- Thời gian kết thúc phải sau thời gian bắt đầu
- Không được có 2 flash sale hoạt động cùng lúc trong cùng khoảng thời gian
- Giá flash sale phải thấp hơn giá gốc
- Không thể sửa/xóa flash sale đã bắt đầu

### 2. Stock Management
- Khi có order, cập nhật `soldCount` trong `FlashSaleItem`
- Kiểm tra `stockLimit` trước khi cho phép đặt hàng
- Nếu không có `stockLimit`, sử dụng stock của sản phẩm

### 3. Pricing Rules
- Flash sale có ưu tiên cao nhất trong việc tính giá
- Sản phẩm trong flash sale không thể áp dụng discount khác
- Khi hết flash sale, sản phẩm sẽ trở lại áp dụng discount thông thường (nếu có)

---

# DISCOUNT API

## Tổng quan Discount
Chức năng Discount cho phép admin tạo và quản lý các chương trình giảm giá cho sản phẩm và danh mục.

## API Endpoints

### Admin Endpoints

#### 1. Tạo Discount
```
POST /api/admin/discounts
```
**Request Body:**
```json
{
  "type": "PRODUCT",
  "value": 25.0,
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-01-31T23:59:59",
  "isActive": true,
  "priority": 3
}
```

#### 2. Cập nhật Discount
```
PUT /api/admin/discounts/{id}
```

#### 3. Xóa Discount
```
DELETE /api/admin/discounts/{id}
```

#### 4. Lấy tất cả Discounts
```
GET /api/admin/discounts?page=0&size=10&sortBy=id&sortDir=desc
```

#### 5. Lấy Active Discounts
```
GET /api/admin/discounts/active
```

#### 6. Lấy Expired Discounts
```
GET /api/admin/discounts/expired
```

#### 7. Lấy Upcoming Discounts
```
GET /api/admin/discounts/upcoming
```

#### 8. Lấy Product Discounts
```
GET /api/admin/discounts/product-discounts
```

#### 9. Lấy Category Discounts
```
GET /api/admin/discounts/category-discounts
```

#### 10. Toggle trạng thái Discount
```
PUT /api/admin/discounts/{id}/toggle-status
```
**Response:**
```json
{
  "result": "SUCCESS",
  "message": "Discount status toggled successfully",
  "data": null
}
```