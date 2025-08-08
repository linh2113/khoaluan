# DISCOUNT API DOCUMENTATION

## Tổng quan
Hệ thống Discount cho phép quản lý các chương trình giảm giá cho sản phẩm và danh mục. Hỗ trợ hai loại discount:
- **PRODUCT**: Giảm giá cho sản phẩm cụ thể với giá cố định
- **CATEGORY**: Giảm giá theo phần trăm cho toàn bộ danh mục

## Base URL
```
Admin APIs: /api/admin
Public APIs: /api
```

## Authentication
Tất cả admin APIs yêu cầu Bearer token trong header:
```
Authorization: Bearer {your_jwt_token}
```

---

## ADMIN APIs

### 1. Quản lý Discount

#### Tạo Discount mới
```http
POST /api/admin/discounts
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "Giảm giá mùa hè 2024",
  "type": "PRODUCT",
  "value": 25.0,
  "startDate": "2024-07-01T00:00:00",
  "endDate": "2024-07-31T23:59:59",
  "isActive": true,
  "productIds": [1, 2, 3],
  "discountedPrices": {
    "1": 15000000,
    "2": 8000000,
    "3": 12000000
  }
}
```

**Response:**
```json
{
  "result": "SUCCESS",
  "message": "Discount created successfully",
  "data": {
    "id": 1,
    "name": "Giảm giá mùa hè 2024",
    "type": "PRODUCT",
    "value": 25.0,
    "startDate": "2024-07-01T00:00:00",
    "endDate": "2024-07-31T23:59:59",
    "isActive": true,
    "assignedCount": 3
  }
}
```

#### Cập nhật Discount
```http
PUT /api/admin/discounts/{id}
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "Giảm giá mùa hè 2024 - Cập nhật",
  "value": 30.0,
  "startDate": "2024-07-01T00:00:00",
  "endDate": "2024-08-15T23:59:59",
  "isActive": true
}
```

#### Lấy danh sách Discounts
```http
GET /api/admin/discounts?search={searchTerm}&page={page}&size={size}&sortBy={sortBy}&sortDir={sortDir}
Authorization: Bearer {token}
```

**Query Parameters:**
- `search` (optional): Tìm kiếm theo tên
- `page` (default: 0): Số trang
- `size` (default: 10): Số item mỗi trang
- `sortBy` (default: "id"): Trường sắp xếp
- `sortDir` (default: "desc"): Hướng sắp xếp

#### Lấy Discount theo ID
```http
GET /api/admin/discounts/{id}
Authorization: Bearer {token}
```

### 2. Quản lý trạng thái Discount

#### Lấy Active Discounts
```http
GET /api/admin/discounts/active
Authorization: Bearer {token}
```

#### Lấy Expired Discounts
```http
GET /api/admin/discounts/expired
Authorization: Bearer {token}
```

#### Lấy Upcoming Discounts
```http
GET /api/admin/discounts/upcoming
Authorization: Bearer {token}
```

#### Lấy Product Discounts
```http
GET /api/admin/discounts/product-discounts
Authorization: Bearer {token}
```

### 3. Gán Discount cho sản phẩm/danh mục

#### Gán Discount cho nhiều sản phẩm
```http
POST /api/admin/discounts/{discountId}/assign-products
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "productIds": [1, 2, 3, 4],
  "discountedPrices": {
    "1": 15000000,
    "2": 8000000,
    "3": 12000000,
    "4": 20000000
  }
}
```

#### Gán Discount cho nhiều danh mục
```http
POST /api/admin/discounts/{discountId}/assign-categories
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "categoryIds": [1, 2, 3]
}
```

### 4. Xóa sản phẩm/danh mục khỏi Discount

#### Xóa sản phẩm khỏi Discount
```http
DELETE /api/admin/discounts/{discountId}/products
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "productIds": [1, 2]
}
```

#### Xóa danh mục khỏi Discount
```http
DELETE /api/admin/discounts/{discountId}/categories
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "categoryIds": [1, 2]
}
```

### 5. Cập nhật giá Discount

#### Cập nhật giá discount cho nhiều sản phẩm
```http
PUT /api/admin/discounts/{discountId}/products/prices
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "productPrices": {
    "1": 14000000,
    "2": 7500000,
    "3": 11500000
  }
}
```

### 6. Utility APIs

#### Lấy sản phẩm đủ điều kiện áp dụng discount
```http
GET /api/admin/products/eligible-for-discount?search={searchTerm}&page={page}&size={size}
Authorization: Bearer {token}
```

---

## PUBLIC APIs

### Lấy thông tin discount của sản phẩm
Thông tin discount được tích hợp trong các API sản phẩm:

#### Lấy chi tiết sản phẩm (có thông tin discount)
```http
GET /api/products/{id}
```

#### Lấy danh sách sản phẩm có discount
```http
GET /api/products?isDiscount=true&page=0&size=12
```

**Response sẽ bao gồm thông tin discount:**
```json
{
  "result": "SUCCESS",
  "message": "Products retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "iPhone 15 Pro Max",
        "price": 30000000,
        "discountedPrice": 25000000,
        "discountPercentage": 16.67,
        "hasDiscount": true,
        "discountInfo": {
          "discountId": 1,
          "discountName": "Giảm giá mùa hè 2024",
          "discountValue": 25.0,
          "startDate": "2024-07-01T00:00:00",
          "endDate": "2024-07-31T23:59:59"
        }
      }
    ]
  }
}
```

---

## Data Models

### DiscountDTO
```json
{
  "id": 1,
  "name": "Giảm giá mùa hè 2024",
  "type": "PRODUCT",
  "value": 25.0,
  "startDate": "2024-07-01T00:00:00",
  "endDate": "2024-07-31T23:59:59",
  "isActive": true,
  "isEffective": true,
  "productIds": [1, 2, 3],
  "productNames": ["iPhone 15", "Samsung Galaxy S24"],
  "discountedPrices": {
    "1": 15000000,
    "2": 8000000
  },
  "categoryIds": [1, 2],
  "categoryNames": ["Smartphone", "Laptop"],
  "assignedCount": 3,
  "createdAt": "2024-06-01T10:00:00",
  "updatedAt": "2024-06-01T10:00:00"
}
```

### DiscountUpdateDTO
```json
{
  "name": "Giảm giá mùa hè 2024 - Cập nhật",
  "value": 30.0,
  "startDate": "2024-07-01T00:00:00",
  "endDate": "2024-08-15T23:59:59",
  "isActive": true,
  "discountedPrice": 14000000
}
```

### BatchAssignRequest
```json
{
  "productIds": [1, 2, 3, 4],
  "discountedPrices": {
    "1": 15000000,
    "2": 8000000,
    "3": 12000000,
    "4": 20000000
  }
}
```

---

## Error Handling

### Standard Error Response
```json
{
  "result": "ERROR",
  "message": "Error description",
  "data": null
}
```

### HTTP Status Codes
- **200**: Success
- **400**: Bad Request (validation errors)
- **401**: Unauthorized
- **404**: Not Found
- **409**: Conflict (time overlap, duplicate)
- **500**: Internal Server Error

---

## Business Rules

### Validation Rules
- Thời gian bắt đầu phải sau thời gian hiện tại (khi tạo mới)
- Thời gian kết thúc phải sau thời gian bắt đầu
- Giá trị discount phải từ 0-100%
- Không thể gán discount cho sản phẩm đã có discount khác trong cùng thời gian
- Sản phẩm trong Flash Sale không thể áp dụng discount thông thường

### Priority Rules
- Flash Sale có ưu tiên cao nhất
- Product Discount có ưu tiên cao hơn Category Discount
- Nếu có nhiều discount cùng loại, áp dụng discount có giá trị cao nhất

### Price Calculation
- **Product Discount**: Sử dụng `discountedPrice` cố định
- **Category Discount**: `finalPrice = originalPrice * (1 - discountValue/100)`
