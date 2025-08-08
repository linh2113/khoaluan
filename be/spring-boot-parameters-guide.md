# Hướng dẫn sử dụng các loại tham số trong Spring Boot

## 1. @PathVariable - Tham số trong đường dẫn URL

**Định nghĩa trong Controller:**
```java
@GetMapping("/users/{id}")
public ResponseEntity<UserDTO> getUser(@PathVariable Integer id) {
    // Xử lý lấy thông tin user theo id
}
```

**Cách gọi API:**
- URL: `http://localhost:8080/users/123`
- Method: GET

## 2. @RequestParam - Tham số query trong URL

**Định nghĩa trong Controller:**
```java
@GetMapping("/products")
public ResponseEntity<Page<ProductDTO>> getProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String keyword
) {
    // Xử lý lấy danh sách sản phẩm với phân trang và tìm kiếm
}
```

**Cách gọi API:**
- URL: `http://localhost:8080/products?page=0&size=20&keyword=iphone`
- Method: GET

## 3. @RequestBody - Dữ liệu JSON trong body

**Định nghĩa trong Controller:**
```java
@PostMapping("/products")
public ResponseEntity<ApiResponse<?>> createProduct(@Valid @RequestBody ProductCreateDTO productCreateDTO) {
    // Xử lý tạo sản phẩm mới
}
```

**Cách gọi API:**
- URL: `http://localhost:8080/products`
- Method: POST
- Content-Type: application/json
- Body:
```json
{
  "name": "iPhone 15",
  "price": 999.99,
  "description": "Latest iPhone model",
  "categoryId": 1,
  "brandId": 2
}
```

## 4. @RequestPart - Upload file kết hợp với dữ liệu JSON

**Định nghĩa trong Controller:**
```java
@PostMapping(value = "/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ApiResponse<?>> createCategory(
    @RequestPart("category") @Valid CategoryDTO categoryDTO,
    @RequestParam(value = "image", required = false) MultipartFile image
) {
    // Xử lý tạo danh mục mới với hình ảnh
}
```

**Cách gọi API:**
- URL: `http://localhost:8080/categories`
- Method: POST
- Content-Type: multipart/form-data
- Body (form-data):
  - Key: category, Type: Text, Value: `{"categoryName":"Electronics","status":true}`
  - Key: image, Type: File, Value: [chọn file từ máy tính]

## 5. @RequestHeader - Tham số trong header

**Định nghĩa trong Controller:**
```java
@GetMapping("/secure-resource")
public ResponseEntity<Resource> getResource(@RequestHeader("Authorization") String authHeader) {
    // Xử lý yêu cầu với header Authorization
}
```

**Cách gọi API:**
- URL: `http://localhost:8080/secure-resource`
- Method: GET
- Headers:
  - Key: Authorization
  - Value: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

## 6. @CookieValue - Giá trị từ cookie

**Định nghĩa trong Controller:**
```java
@GetMapping("/welcome")
public String welcome(@CookieValue(value = "userId", required = false) String userId) {
    // Xử lý dựa trên cookie userId
}
```

**Cách gọi API:**
- URL: `http://localhost:8080/welcome`
- Method: GET
- Cookies: userId=123456

## 7. Ví dụ thực tế: Cập nhật danh mục với hình ảnh

**Định nghĩa trong Controller:**
```java
@PutMapping(value = "/categories/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ApiResponse<?>> updateCategory(
    @PathVariable Integer id,
    @RequestPart("category") @Valid CategoryUpdateDTO categoryUpdateDTO,
    @RequestParam(value = "image", required = false) MultipartFile image
) {
    // Xử lý cập nhật danh mục
}
```

**Cách gọi API với Postman:**
1. Chọn phương thức PUT
2. URL: `http://localhost:8080/admin/categories/123` (123 là id của danh mục)
3. Body > form-data:
   - Key: category, Type: Text, Value: `{"categoryName":"Electronics Updated","status":true}`
   - Key: image, Type: File, Value: [chọn file từ máy tính]

**Cách gọi API với cURL:**
```bash
curl -X PUT \
  http://localhost:8080/admin/categories/123 \
  -H 'Content-Type: multipart/form-data' \
  -F 'category={"categoryName":"Electronics Updated","status":true}' \
  -F 'image=@/đường/dẫn/đến/file/ảnh.jpg'
```

**Cách gọi API với JavaScript (Fetch API):**
```javascript
const formData = new FormData();
formData.append('category', JSON.stringify({
  categoryName: 'Electronics Updated',
  status: true
}));
formData.append('image', fileInput.files[0]); // fileInput là element input type=file

fetch('http://localhost:8080/admin/categories/123', {
  method: 'PUT',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```