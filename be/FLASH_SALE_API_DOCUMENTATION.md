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

-  `id`: ID duy nhất
-  `name`: Tên flash sale
-  `description`: Mô tả
-  `startTime`: Thời gian bắt đầu
-  `endTime`: Thời gian kết thúc
-  `createdAt`: Thời gian tạo
-  `updatedAt`: Thời gian cập nhật

### FlashSaleItem

-  `id`: ID duy nhất
-  `flashSale`: Flash sale liên kết
-  `product`: Sản phẩm liên kết
-  `flashPrice`: Giá flash sale
-  `stockLimit`: Giới hạn số lượng (null = không giới hạn)
-  `soldCount`: Số lượng đã bán

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

-  Thời gian bắt đầu phải sau thời gian hiện tại
-  Thời gian kết thúc phải sau thời gian bắt đầu
-  Không được có 2 flash sale hoạt động cùng lúc trong cùng khoảng thời gian
-  Giá flash sale phải thấp hơn giá gốc
-  Không thể sửa/xóa flash sale đã bắt đầu

### 2. Stock Management

-  Khi có order, cập nhật `soldCount` trong `FlashSaleItem`
-  Kiểm tra `stockLimit` trước khi cho phép đặt hàng
-  Nếu không có `stockLimit`, sử dụng stock của sản phẩm

### 3. Pricing Rules

-  Flash sale có ưu tiên cao nhất trong việc tính giá
-  Sản phẩm trong flash sale không thể áp dụng discount khác
-  Khi hết flash sale, sản phẩm sẽ trở lại áp dụng discount thông thường (nếu có)

---

# DISCOUNT API

## Tổng quan Discount

Hệ thống Discount cho phép admin tạo và quản lý các chương trình giảm giá cho sản phẩm và danh mục. Hệ thống hỗ trợ hai loại discount chính:

-  **PRODUCT**: Giảm giá cho sản phẩm cụ thể
-  **CATEGORY**: Giảm giá cho toàn bộ danh mục sản phẩm

## Entities

### Discount

-  `id`: ID duy nhất
-  `name`: Tên chương trình giảm giá
-  `type`: Loại discount (PRODUCT/CATEGORY)
-  `value`: Giá trị giảm giá (%)
-  `startDate`: Thời gian bắt đầu
-  `endDate`: Thời gian kết thúc
-  `isActive`: Trạng thái hoạt động
-  `createdAt`: Thời gian tạo
-  `updatedAt`: Thời gian cập nhật

### ProductDiscount

-  `id`: ID duy nhất
-  `product`: Sản phẩm liên kết
-  `discount`: Discount liên kết
-  `discountedPrice`: Giá sau khi giảm (VND)
-  `createdAt`: Thời gian tạo

### CategoryDiscount

-  `id`: ID duy nhất
-  `category`: Danh mục liên kết
-  `discount`: Discount liên kết
-  `createdAt`: Thời gian tạo

## API Endpoints

### Admin Endpoints

#### 1. Tạo Discount

```
POST /api/admin/discounts
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

#### 2. Cập nhật Discount

```
PUT /api/admin/discounts/{id}
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

#### 3. Lấy tất cả Discounts (với phân trang và tìm kiếm)

```
GET /api/admin/discounts?search={searchTerm}&page={page}&size={size}&sortBy={sortBy}&sortDir={sortDir}
```

**Parameters:**

-  `search` (optional): Tìm kiếm theo tên discount
-  `page` (default: 0): Số trang
-  `size` (default: 10): Số lượng item mỗi trang
-  `sortBy` (default: "id"): Trường sắp xếp
-  `sortDir` (default: "desc"): Hướng sắp xếp (asc/desc)

#### 4. Lấy Discount theo ID

```
GET /api/admin/discounts/{id}
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

#### 9. Gán Discount cho nhiều sản phẩm

```
POST /api/admin/discounts/{discountId}/assign-products
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

**Response:**

```json
{
   "result": "SUCCESS",
   "message": "Successfully assigned 4 out of 4 products",
   "data": {
      "successCount": 4,
      "totalCount": 4
   }
}
```

#### 10. Gán Discount cho nhiều danh mục

```
POST /api/admin/discounts/{discountId}/assign-categories
```

**Request Body:**

```json
{
   "categoryIds": [1, 2, 3]
}
```

**Response:**

```json
{
   "result": "SUCCESS",
   "message": "Successfully assigned 3 out of 3 categories",
   "data": {
      "successCount": 3,
      "totalCount": 3
   }
}
```

#### 11. Xóa sản phẩm khỏi Discount

```
DELETE /api/admin/discounts/{discountId}/products
```

**Request Body:**

```json
{
   "productIds": [1, 2]
}
```

#### 12. Xóa danh mục khỏi Discount

```
DELETE /api/admin/discounts/{discountId}/categories
```

**Request Body:**

```json
{
   "categoryIds": [1, 2]
}
```

#### 13. Cập nhật giá discount cho nhiều sản phẩm

```
PUT /api/admin/discounts/{discountId}/products/prices
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

#### 14. Lấy sản phẩm đủ điều kiện áp dụng discount

```
GET /api/admin/products/eligible-for-discount?search={searchTerm}&page={page}&size={size}&sortBy={sortBy}&sortDir={sortDir}
```

### Public Endpoints (User)

Discount information được tích hợp vào các API sản phẩm public, không có endpoint riêng cho discount.

#### 1. Lấy thông tin discount của sản phẩm

Thông tin discount được bao gồm trong response của:

```
GET /api/products/{id}
GET /api/products (với filter isDiscount=true)
```

**Response sẽ bao gồm:**

```json
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
```

## Business Rules

### 1. Validation Rules

-  Thời gian bắt đầu phải sau thời gian hiện tại (khi tạo mới)
-  Thời gian kết thúc phải sau thời gian bắt đầu
-  Giá trị discount phải từ 0-100%
-  Không thể gán discount cho sản phẩm đã có discount khác trong cùng khoảng thời gian
-  Không thể gán discount cho danh mục đã có discount khác trong cùng khoảng thời gian
-  Sản phẩm trong Flash Sale không thể áp dụng discount thông thường

### 2. Priority Rules

-  Flash Sale có ưu tiên cao nhất
-  Product Discount có ưu tiên cao hơn Category Discount
-  Nếu có nhiều discount cùng loại và cùng thời gian, áp dụng discount có giá trị cao nhất

### 3. Price Calculation

-  **Product Discount**: Sử dụng `discountedPrice` cố định
-  **Category Discount**: Tính theo phần trăm từ giá gốc
-  **Formula**: `finalPrice = originalPrice * (1 - discountValue/100)`

### 4. Time Management

-  **Active**: `startDate <= now <= endDate && isActive = true`
-  **Upcoming**: `startDate > now`
-  **Expired**: `endDate < now`

## DTOs

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
   "productNames": ["iPhone 15", "Samsung Galaxy S24", "MacBook Pro"],
   "discountedPrices": {
      "1": 15000000,
      "2": 8000000,
      "3": 12000000
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

## Error Handling

### Common Error Responses

```json
{
   "result": "ERROR",
   "message": "Error description",
   "data": null
}
```

### Error Cases

-  **400 Bad Request**: Invalid input data, validation errors
-  **404 Not Found**: Discount/Product/Category not found
-  **409 Conflict**: Discount time overlap, duplicate assignment
-  **500 Internal Server Error**: Server errors

## Frontend Integration Guide

### 1. Admin Panel - Discount Management

#### Tạo Discount mới

```javascript
// Tạo Product Discount
const createProductDiscount = async (discountData) => {
   const response = await fetch("/api/admin/discounts", {
      method: "POST",
      headers: {
         "Content-Type": "application/json",
         Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
         name: discountData.name,
         type: "PRODUCT",
         value: discountData.value,
         startDate: discountData.startDate,
         endDate: discountData.endDate,
         isActive: true,
         productIds: discountData.selectedProducts,
         discountedPrices: discountData.productPrices,
      }),
   });
   return response.json();
};

// Tạo Category Discount
const createCategoryDiscount = async (discountData) => {
   const response = await fetch("/api/admin/discounts", {
      method: "POST",
      headers: {
         "Content-Type": "application/json",
         Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
         name: discountData.name,
         type: "CATEGORY",
         value: discountData.value,
         startDate: discountData.startDate,
         endDate: discountData.endDate,
         isActive: true,
         categoryIds: discountData.selectedCategories,
      }),
   });
   return response.json();
};
```

#### Lấy danh sách Discounts với phân trang

```javascript
const getDiscounts = async (page = 0, size = 10, search = "", sortBy = "id", sortDir = "desc") => {
   const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      sortDir,
   });

   if (search) {
      params.append("search", search);
   }

   const response = await fetch(`/api/admin/discounts?${params}`, {
      headers: {
         Authorization: `Bearer ${token}`,
      },
   });
   return response.json();
};
```

#### Gán sản phẩm vào Discount

```javascript
const assignProductsToDiscount = async (discountId, productIds, discountedPrices) => {
   const response = await fetch(`/api/admin/discounts/${discountId}/assign-products`, {
      method: "POST",
      headers: {
         "Content-Type": "application/json",
         Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
         productIds,
         discountedPrices,
      }),
   });
   return response.json();
};
```

#### Lấy sản phẩm đủ điều kiện

```javascript
const getEligibleProducts = async (page = 0, size = 10, search = "") => {
   const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
   });

   if (search) {
      params.append("search", search);
   }

   const response = await fetch(`/api/admin/products/eligible-for-discount?${params}`, {
      headers: {
         Authorization: `Bearer ${token}`,
      },
   });
   return response.json();
};
```

### 2. User Interface - Hiển thị Discount

#### Lấy sản phẩm có discount

```javascript
const getDiscountedProducts = async (page = 0, size = 12) => {
   const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      isDiscount: "true",
   });

   const response = await fetch(`/api/products?${params}`);
   return response.json();
};
```

#### Hiển thị thông tin discount trên product card

```javascript
const ProductCard = ({ product }) => {
   const hasDiscount = product.hasDiscount;
   const originalPrice = product.price;
   const discountedPrice = product.discountedPrice;
   const discountPercentage = product.discountPercentage;

   return (
      <div className="product-card">
         <h3>{product.name}</h3>
         <div className="price-section">
            {hasDiscount ? (
               <>
                  <span className="original-price">{originalPrice.toLocaleString()}đ</span>
                  <span className="discounted-price">{discountedPrice.toLocaleString()}đ</span>
                  <span className="discount-badge">-{discountPercentage}%</span>
               </>
            ) : (
               <span className="price">{originalPrice.toLocaleString()}đ</span>
            )}
         </div>
         {hasDiscount && product.discountInfo && (
            <div className="discount-info">
               <span className="discount-name">{product.discountInfo.discountName}</span>
               <span className="discount-end">
                  Kết thúc: {new Date(product.discountInfo.endDate).toLocaleDateString()}
               </span>
            </div>
         )}
      </div>
   );
};
```

### 3. Validation và Error Handling

#### Frontend Validation

```javascript
const validateDiscountForm = (formData) => {
   const errors = {};

   if (!formData.name || formData.name.trim() === "") {
      errors.name = "Tên discount là bắt buộc";
   }

   if (!formData.value || formData.value < 0 || formData.value > 100) {
      errors.value = "Giá trị discount phải từ 0-100%";
   }

   if (!formData.startDate) {
      errors.startDate = "Ngày bắt đầu là bắt buộc";
   }

   if (!formData.endDate) {
      errors.endDate = "Ngày kết thúc là bắt buộc";
   }

   if (
      formData.startDate &&
      formData.endDate &&
      new Date(formData.startDate) >= new Date(formData.endDate)
   ) {
      errors.endDate = "Ngày kết thúc phải sau ngày bắt đầu";
   }

   if (formData.type === "PRODUCT" && (!formData.productIds || formData.productIds.length === 0)) {
      errors.products = "Phải chọn ít nhất 1 sản phẩm";
   }

   if (
      formData.type === "CATEGORY" &&
      (!formData.categoryIds || formData.categoryIds.length === 0)
   ) {
      errors.categories = "Phải chọn ít nhất 1 danh mục";
   }

   return errors;
};
```

#### Error Handling

```javascript
const handleApiError = (error) => {
   if (error.status === 400) {
      // Validation errors
      showNotification("Dữ liệu không hợp lệ", "error");
   } else if (error.status === 404) {
      // Not found
      showNotification("Không tìm thấy dữ liệu", "error");
   } else if (error.status === 409) {
      // Conflict (overlap, duplicate)
      showNotification("Có xung đột thời gian hoặc dữ liệu trùng lặp", "error");
   } else {
      // Server error
      showNotification("Có lỗi xảy ra, vui lòng thử lại", "error");
   }
};
```

## Testing

### API Testing với Postman/Insomnia

#### Test Case 1: Tạo Product Discount

```
POST /api/admin/discounts
Content-Type: application/json
Authorization: Bearer {token}

{
  "name": "Test Product Discount",
  "type": "PRODUCT",
  "value": 20.0,
  "startDate": "2024-07-01T00:00:00",
  "endDate": "2024-07-31T23:59:59",
  "isActive": true,
  "productIds": [1, 2],
  "discountedPrices": {
    "1": 15000000,
    "2": 8000000
  }
}
```

#### Test Case 2: Gán thêm sản phẩm vào Discount

```
POST /api/admin/discounts/1/assign-products
Content-Type: application/json
Authorization: Bearer {token}

{
  "productIds": [3, 4],
  "discountedPrices": {
    "3": 12000000,
    "4": 20000000
  }
}
```

#### Test Case 3: Lấy sản phẩm có discount

```
GET /api/products?isDiscount=true&page=0&size=10
```

---

## Tổng kết

Hệ thống Discount cung cấp đầy đủ các chức năng quản lý giảm giá cho cả admin và user:

### Admin Features:

-  Tạo, sửa, xóa discount
-  Gán discount cho sản phẩm/danh mục
-  Quản lý thời gian và trạng thái
-  Xem báo cáo và thống kê

### User Features:

-  Xem sản phẩm có discount
-  Lọc sản phẩm theo discount
-  Xem thông tin chi tiết discount

### Technical Features:

-  Validation đầy đủ
-  Error handling
-  Phân trang và tìm kiếm
-  Tích hợp với Flash Sale
-  Tối ưu performance
