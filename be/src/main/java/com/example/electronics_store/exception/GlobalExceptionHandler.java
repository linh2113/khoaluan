package com.example.electronics_store.exception;

import java.io.IOException;
import java.time.format.DateTimeParseException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.electronics_store.dto.ApiResponse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<?>> handleIOException(IOException ex) {
        String message = "Lỗi xử lý file";
        if (ex.getMessage().contains("upload")) {
            message = "Tải file lên thất bại";
        } else if (ex.getMessage().contains("image")) {
            message = "Lỗi xử lý hình ảnh";
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<?>> handleNumberFormatException(NumberFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Định dạng số không hợp lệ"));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiResponse<?>> handleDateTimeParseException(DateTimeParseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Định dạng ngày tháng không hợp lệ"));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra trong hệ thống"));
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ApiResponse<?>> handleClassCastException(ClassCastException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Lỗi chuyển đổi dữ liệu"));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        String message = translateErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        String message = translateErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Không tìm thấy dữ liệu yêu cầu"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = "Không thể thực hiện thao tác do vi phạm ràng buộc dữ liệu";
        if (ex.getMessage().contains("foreign key")) {
            message = "Không thể xóa do có dữ liệu liên quan";
        } else if (ex.getMessage().contains("unique")) {
            message = "Dữ liệu đã tồn tại trong hệ thống";
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Dữ liệu gửi lên không đúng định dạng"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Thiếu tham số bắt buộc: " + ex.getParameterName()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        StringBuilder errors = new StringBuilder("Dữ liệu không hợp lệ: ");
        ex.getConstraintViolations().forEach(violation -> 
            errors.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errors.toString()));
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Tên đăng nhập hoặc mật khẩu không đúng"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Tên đăng nhập hoặc mật khẩu không đúng"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Bạn không có quyền truy cập tài nguyên này"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Dữ liệu không hợp lệ: " + errors.toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra trong hệ thống. Vui lòng thử lại sau"));
    }

    private String translateErrorMessage(String message) {
    if (message == null) return "Có lỗi xảy ra";
    
    // Authentication & Authorization errors
    if (message.contains("Invalid username or password")) return "Tên đăng nhập hoặc mật khẩu không đúng";
    if (message.contains("Account is not active")) return "Tài khoản chưa được kích hoạt";
    if (message.contains("Account has been locked")) return "Tài khoản đã bị khóa do đăng nhập sai quá nhiều lần";
    if (message.contains("Email not verified")) return "Email chưa được xác thực";
    if (message.contains("Token expired")) return "Mã xác thực đã hết hạn";
    if (message.contains("Invalid token")) return "Mã xác thực không hợp lệ";
    
    // User errors
    if (message.contains("User not found")) return "Không tìm thấy người dùng";
    if (message.contains("Username already exists")) return "Tên đăng nhập đã tồn tại";
    if (message.contains("Email already exists")) return "Email đã được sử dụng";
    if (message.contains("Current password is incorrect")) return "Mật khẩu hiện tại không đúng";
    if (message.contains("New password cannot be the same")) return "Mật khẩu mới không được trùng với mật khẩu cũ";
    
    // Product errors
    if (message.contains("Product not found")) return "Không tìm thấy sản phẩm";
    if (message.contains("Product name already exists")) return "Tên sản phẩm đã tồn tại";
    if (message.contains("Insufficient stock")) return "Không đủ hàng trong kho";
    if (message.contains("Product is out of stock")) return "Sản phẩm đã hết hàng";
    if (message.contains("Cannot delete product with orders")) return "Không thể xóa sản phẩm đã có đơn hàng";
    
    // Category errors
    if (message.contains("Category not found")) return "Không tìm thấy danh mục";
    if (message.contains("Category name already exists")) return "Tên danh mục đã tồn tại";
    if (message.contains("Cannot delete category with products")) return "Không thể xóa danh mục có sản phẩm";
    
    // Brand errors
    if (message.contains("Brand not found")) return "Không tìm thấy thương hiệu";
    if (message.contains("Brand name already exists")) return "Tên thương hiệu đã tồn tại";
    if (message.contains("Cannot delete brand with products")) return "Không thể xóa thương hiệu có sản phẩm";
    
    // Order errors
    if (message.contains("Order not found")) return "Không tìm thấy đơn hàng";
    if (message.contains("Cannot cancel order")) return "Không thể hủy đơn hàng này";
    if (message.contains("Order already cancelled")) return "Đơn hàng đã được hủy";
    if (message.contains("Order already completed")) return "Đơn hàng đã hoàn thành";
    if (message.contains("Invalid order status")) return "Trạng thái đơn hàng không hợp lệ";
    
    // Cart errors
    if (message.contains("Cart item not found")) return "Không tìm thấy sản phẩm trong giỏ hàng";
    if (message.contains("Product already in cart")) return "Sản phẩm đã có trong giỏ hàng";
    if (message.contains("Cart is empty")) return "Giỏ hàng trống";
    
    // Payment & Shipping errors
    if (message.contains("Payment method not found")) return "Không tìm thấy phương thức thanh toán";
    if (message.contains("Payment method name already exists")) return "Tên phương thức thanh toán đã tồn tại";
    if (message.contains("Shipping method not found")) return "Không tìm thấy phương thức vận chuyển";
    if (message.contains("Shipping method name already exists")) return "Tên phương thức vận chuyển đã tồn tại";
    if (message.contains("Payment failed")) return "Thanh toán thất bại";
    
    // Discount errors
    if (message.contains("Discount not found")) return "Không tìm thấy mã giảm giá";
    if (message.contains("Discount code already exists")) return "Mã giảm giá đã tồn tại";
    if (message.contains("Discount expired")) return "Mã giảm giá đã hết hạn";
    if (message.contains("Discount not active")) return "Mã giảm giá chưa được kích hoạt";
    if (message.contains("Discount usage limit exceeded")) return "Mã giảm giá đã hết lượt sử dụng";
    if (message.contains("User already used this discount")) return "Bạn đã sử dụng mã giảm giá này";
    if (message.contains("Minimum order amount not met")) return "Đơn hàng chưa đạt giá trị tối thiểu";
    
    // Flash Sale errors
    if (message.contains("Flash sale not found")) return "Không tìm thấy flash sale";
    if (message.contains("Flash sale expired")) return "Flash sale đã kết thúc";
    if (message.contains("Flash sale not started")) return "Flash sale chưa bắt đầu";
    if (message.contains("Flash sale item not found")) return "Không tìm thấy sản phẩm trong flash sale";
    if (message.contains("Flash sale quantity exceeded")) return "Số lượng flash sale đã hết";
    
    // Rating errors
    if (message.contains("Rating not found")) return "Không tìm thấy đánh giá";
    if (message.contains("User already rated this product")) return "Bạn đã đánh giá sản phẩm này";
    if (message.contains("Cannot rate without purchase")) return "Bạn cần mua sản phẩm trước khi đánh giá";
    
    // File upload errors
    if (message.contains("Failed to upload image")) return "Tải ảnh lên thất bại";
    if (message.contains("Invalid file format")) return "Định dạng file không hợp lệ";
    if (message.contains("File size too large")) return "Kích thước file quá lớn";
    if (message.contains("Failed to upload banner")) return "Tải ảnh banner lên thất bại";
    
    // General errors
    if (message.contains("not found")) return "Không tìm thấy dữ liệu";
    if (message.contains("already exists")) return "Dữ liệu đã tồn tại";
    if (message.contains("Cannot delete")) return "Không thể xóa do có dữ liệu liên quan";
    if (message.contains("Access denied")) return "Bạn không có quyền truy cập";
    if (message.contains("Unauthorized")) return "Bạn cần đăng nhập để thực hiện thao tác này";
    
    return message; // Return original if no translation found

}
}