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
- `flashSaleId`: ID flash sale
- `productId`: ID sản phẩm
- `flashPrice`: Giá flash sale
- `stockLimit`: Giới hạn số lượng
- `soldCount`: Số lượng đã bán

## API Endpoints

### Admin Endpoints

#### 1. Tạo Flash Sale
```
POST /api/flash-sales/admin
```
**Request Body:**
```json
{
  "name": "Flash Sale Tết 2024",
  "description": "Giảm giá đặc biệt dịp Tết",
  "startTime": "2024-02-01T00:00:00",
  "endTime": "2024-02-07T23:59:59"
}
```

#### 2. Cập nhật Flash Sale
```
PUT /api/flash-sales/admin/{id}
```

#### 3. Xóa Flash Sale
```
DELETE /api/flash-sales/admin/{id}
```

#### 4. Lấy tất cả Flash Sales
```
GET /api/flash-sales/admin
```

#### 5. Thêm sản phẩm vào Flash Sale
```
POST /api/flash-sales/admin/{flashSaleId}/products
```
**Request Body:**
```json
{
  "productId": 1,
  "flashPrice": 500000,
  "stockLimit": 100
}
```

#### 6. Cập nhật sản phẩm trong Flash Sale
```
PUT /api/flash-sales/admin/items/{flashSaleItemId}
```

#### 7. Xóa sản phẩm khỏi Flash Sale
```
DELETE /api/flash-sales/admin/items/{flashSaleItemId}
```

#### 8. Lấy danh sách Flash Sale Items
```
GET /api/flash-sales/admin/{flashSaleId}/items
```

#### 9. Tìm kiếm Flash Sale theo tên
```
GET /api/flash-sales/admin/search?name={name}
```

#### 10. Thống kê số lượng Flash Sale đang hoạt động
```
GET /api/flash-sales/admin/stats/active-count
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

### 3. Priority System
- Flash sale có ưu tiên cao nhất trong việc tính giá
- Sản phẩm trong flash sale không thể áp dụng discount khác

## Integration Points

### 1. Order Service
- Kiểm tra flash sale khi tạo order
- Cập nhật `soldCount` khi order thành công
- Validate stock limit
- Lưu giá flash sale vào OrderDetail khi tạo đơn hàng

### 2. Cart Service
- Hiển thị giá flash sale trong cart
- Kiểm tra tính khả dụng của flash sale

### 3. Product Service
- Hiển thị thông tin flash sale trong danh sách sản phẩm
- Filter sản phẩm flash sale

### 4. Discount Service
- Tính giá ưu tiên flash sale trước discount

## Error Handling

### Common Error Codes
- `400 Bad Request`: Dữ liệu không hợp lệ
- `404 Not Found`: Flash sale không tồn tại
- `409 Conflict`: Xung đột thời gian flash sale
- `422 Unprocessable Entity`: Vi phạm business rules

### Error Messages
- "Flash sale not found"
- "There is already an active flash sale in this time range"
- "Cannot update flash sale that has already started"
- "Flash price must be lower than original price"
- "Not enough flash sale stock for product"

## Database Schema

### flash_sales table
```sql
CREATE TABLE flash_sales (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### flash_sale_items table
```sql
CREATE TABLE flash_sale_items (
    id SERIAL PRIMARY KEY,
    flash_sale_id INTEGER NOT NULL REFERENCES flash_sales(id),
    product_id INTEGER NOT NULL REFERENCES products(id),
    flash_price DECIMAL(10,2) NOT NULL,
    stock_limit INTEGER,
    sold_count INTEGER DEFAULT 0
);
```

---

# DISCOUNT API

## Tổng quan Discount
Hệ thống Discount cho phép admin tạo và quản lý các chương trình giảm giá cho sản phẩm và danh mục sản phẩm.

## Entities

### Discount
- `id`: ID duy nhất
- `type`: Loại discount (PRODUCT/CATEGORY)
- `value`: Giá trị giảm giá (%)
- `startDate`: Thời gian bắt đầu
- `endDate`: Thời gian kết thúc
- `isActive`: Trạng thái hoạt động
- `priority`: Độ ưu tiên
- `createdAt`: Thời gian tạo
- `updatedAt`: Thời gian cập nhật

### ProductDiscount
- `id`: ID duy nhất
- `discountId`: ID discount
- `productId`: ID sản phẩm
- `discountedPrice`: Giá sau khi giảm
- `createdAt`: Thời gian tạo

### CategoryDiscount
- `id`: ID duy nhất
- `discountId`: ID discount
- `categoryId`: ID danh mục
- `createdAt`: Thời gian tạo

## API Endpoints

### Admin Endpoints

#### 1. Tạo Discount
```
POST /api/v1/admin/discounts
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
PUT /api/v1/admin/discounts/{id}
```

#### 3. Xóa Discount
```
DELETE /api/v1/admin/discounts/{id}
```

#### 4. Lấy tất cả Discounts
```
GET /api/v1/admin/discounts?page=0&size=10&sortBy=id&sortDir=desc
```

#### 5. Lấy Active Discounts
```
GET /api/v1/admin/discounts/active
```

#### 6. Lấy Expired Discounts
```
GET /api/v1/admin/discounts/expired
```

#### 7. Lấy Upcoming Discounts
```
GET /api/v1/admin/discounts/upcoming
```

#### 8. Lấy Product Discounts
```
GET /api/v1/admin/discounts/product-discounts
```

#### 9. Lấy Category Discounts
```
GET /api/v1/admin/discounts/category-discounts
```

#### 10. Gán Discount cho Products
```
POST /api/v1/admin/discounts/{discountId}/assign-products
```
**Request Body:**
```json
{
  "productIds": [1, 2, 3],
  "discountedPrices": {
    "1": 800000,
    "2": 900000,
    "3": 1000000
  }
}
```

#### 11. Gán Discount cho Categories
```
POST /api/v1/admin/discounts/{discountId}/assign-categories
```
**Request Body:**
```json
{
  "categoryIds": [1, 2, 3]
}
```

## Business Rules

### 1. Validation Rules
- Thời gian bắt đầu phải trước thời gian kết thúc
- Giá trị discount phải từ 0-100%
- Product discount chỉ áp dụng cho sản phẩm
- Category discount chỉ áp dụng cho danh mục
- Không thể gán product cho category discount và ngược lại

### 2. Priority System
- Discount có priority cao hơn sẽ được áp dụng trước
- Flash sale có ưu tiên cao nhất
- Product discount có ưu tiên cao hơn category discount
- Một sản phẩm có thể thuộc cả product discount và category discount, nhưng sẽ áp dụng product discount

### 3. Status Management
- Active: Discount đang hoạt động (trong khoảng thời gian)
- Expired: Discount đã hết hạn
- Upcoming: Discount chưa bắt đầu

## Integration Points

### 1. Product Service
- Hiển thị giá đã giảm trong danh sách sản phẩm
- Tính toán giá cuối cùng với discount

### 2. Category Service
- Áp dụng discount cho tất cả sản phẩm trong danh mục
- Hiển thị thông tin discount của danh mục

### 3. Cart Service
- Tính toán giá discount trong giỏ hàng
- Hiển thị thông tin tiết kiệm
- Sử dụng DiscountService để tính giá sản phẩm

### 4. Order Service
- Áp dụng discount khi tạo đơn hàng
- Lưu thông tin discount đã áp dụng
- Lưu giá đã giảm vào OrderDetail khi tạo đơn hàng
- Sử dụng DiscountService để tính giá sản phẩm tại thời điểm đặt hàng

## Centralized Price Calculation

Để đảm bảo tính nhất quán trong việc tính giá, hệ thống sử dụng DiscountService làm điểm trung tâm:

### DiscountService
- `calculateProductPrice(Integer productId)`: Tính giá sản phẩm tại thời điểm hiện tại
- `calculateProductPriceAtTime(Integer productId, LocalDateTime time)`: Tính giá sản phẩm tại thời điểm cụ thể
- `getProductDiscountInfo(Integer productId)`: Lấy thông tin giảm giá của sản phẩm

### Quy trình tính giá
1. Kiểm tra flash sale (ưu tiên cao nhất)
2. Kiểm tra product discount (ưu tiên thứ hai)
3. Kiểm tra category discount (ưu tiên thấp nhất)
4. Trả về giá gốc nếu không có giảm giá nào áp dụng

## Testing

### Unit Tests
- DiscountServiceImpl methods
- FlashSaleServiceImpl methods
- Validation logic
- Business rules
- Assignment operations

### Integration Tests
- API endpoints
- Database operations
- Service integrations
- Bulk operations
- Price calculation across services

### Test Data
```json
{
  "flashSale": {
    "name": "Test Flash Sale",
    "startTime": "2024-12-01T00:00:00",
    "endTime": "2024-12-07T23:59:59"
  },
  "flashSaleItem": {
    "productId": 1,
    "flashPrice": 100000,
    "stockLimit": 50
  },
  "productDiscount": {
    "type": "PRODUCT",
    "value": 25.0,
    "startDate": "2024-01-01T00:00:00",
    "endDate": "2024-01-31T23:59:59",
    "isActive": true,
  },
  "categoryDiscount": {
    "type": "CATEGORY",
    "value": 15.0,
    "startDate": "2024-01-01T00:00:00",
    "endDate": "2024-01-31T23:59:59",
    "isActive": true,
  }
}
