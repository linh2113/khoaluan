# Hướng dẫn tích hợp thanh toán VNPay

## Tổng quan

Tài liệu này mô tả cách tích hợp và sử dụng chức năng thanh toán VNPay trong ứng dụng Electronics Store.

## Quy trình thanh toán

1. **Khởi tạo thanh toán**:
   - Người dùng chọn sản phẩm và đặt hàng
   - Người dùng chọn phương thức thanh toán VNPay
   - Backend tạo URL thanh toán và chuyển hướng người dùng đến cổng thanh toán VNPay

2. **Xử lý thanh toán**:
   - Người dùng nhập thông tin thẻ và xác nhận thanh toán trên cổng VNPay
   - VNPay xử lý giao dịch và chuyển hướng người dùng về trang kết quả thanh toán
   - Backend xử lý kết quả thanh toán và cập nhật trạng thái đơn hàng

3. **Thông báo thanh toán tự động (IPN)**:
   - VNPay gửi thông báo thanh toán tự động đến endpoint IPN
   - Backend xử lý thông báo và cập nhật trạng thái đơn hàng

## API Endpoints

### 1. Tạo URL thanh toán

```http
POST /api/payment/create-payment
```

**Tham số**:
- `orderId`: ID của đơn hàng cần thanh toán
- `amount`: Số tiền cần thanh toán (đơn vị VND)
- `orderInfo`: Thông tin đơn hàng

**Phản hồi**:
```json
{
  "success": true,
  "message": "Payment URL created successfully",
  "data": {
    "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?..."
  },
  "timestamp": "2023-06-01T12:34:56"
}
```

### 2. Xử lý kết quả thanh toán

```http
GET /api/payment/vnpay-return
```

**Tham số**:
- Các tham số được VNPay trả về qua URL

**Phản hồi**:
- Chuyển hướng về trang frontend với kết quả thanh toán

### 3. Xử lý thông báo thanh toán tự động (IPN)

```http
GET /api/payment/vnpay-ipn
```

**Tham số**:
- Các tham số được VNPay gửi đến

**Phản hồi**:
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "data": {
    "success": true,
    "orderId": 123,
    "amount": 1000000,
    "transactionId": "13349437",
    "bankCode": "NCB",
    "paymentDate": "20230601123456",
    "message": "Giao dịch thành công"
  },
  "timestamp": "2023-06-01T12:34:56"
}
```

## Cấu hình VNPay

Để sử dụng VNPay trong môi trường thực tế, bạn cần đăng ký tài khoản merchant với VNPay và cập nhật các thông tin sau trong file `application.properties`:

```properties
vnpay.terminal-id=YOUR_TERMINAL_ID
vnpay.secret-key=YOUR_SECRET_KEY
vnpay.payment-url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html  # Sandbox
# vnpay.payment-url=https://pay.vnpay.vn/vpcpay.html  # Production
app.backend-url=YOUR_BACKEND_URL
```

## Môi trường test

Trong môi trường sandbox, bạn có thể sử dụng các thông tin thẻ test sau:

- **Ngân hàng**: NCB
- **Số thẻ**: 9704198526191432198
- **Tên chủ thẻ**: NGUYEN VAN A
- **Ngày phát hành**: 07/15
- **Mật khẩu OTP**: 123456

## Mã lỗi VNPay

| Mã | Mô tả |
|----|-------|
| 00 | Giao dịch thành công |
| 01 | Giao dịch đã tồn tại |
| 02 | Merchant không hợp lệ |
| 03 | Dữ liệu gửi sang không đúng định dạng |
| 04 | Khởi tạo GD không thành công do Website đang bị tạm khóa |
| 05 | Giao dịch không thành công do: Quý khách nhập sai mật khẩu thanh toán quá số lần quy định |
| 06 | Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch |
| 07 | Giao dịch bị nghi ngờ là giao dịch gian lận |
| 09 | Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking |
| 10 | Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần |
| 11 | Giao dịch không thành công do: Đã hết hạn chờ thanh toán |
| 24 | Giao dịch không thành công do: Khách hàng hủy giao dịch |