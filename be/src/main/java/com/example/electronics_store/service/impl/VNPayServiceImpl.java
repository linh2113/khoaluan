package com.example.electronics_store.service.impl;

import com.example.electronics_store.config.VNPayConfig;
import com.example.electronics_store.dto.PaymentResponseDTO;
import com.example.electronics_store.model.Order;
import com.example.electronics_store.repository.OrderRepository;
import com.example.electronics_store.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayServiceImpl implements VNPayService {

    @Autowired
    private VNPayConfig vnPayConfig;
    
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public String createPaymentUrl(Integer orderId, Long amount, String orderInfo, String ipAddress) {
        try {
            String vnp_TxnRef = orderId + "_" + vnPayConfig.getRandomNumber(8);
            String vnp_TmnCode = vnPayConfig.getVnpTmnCode();
            
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Nhân 100 vì VNPay tính tiền theo đơn vị xu
            vnp_Params.put("vnp_CurrCode", "VND");
            
            // Phương thức thanh toán, mặc định là ATM
            vnp_Params.put("vnp_BankCode", "NCB");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            
            // URL trả về sau khi thanh toán
            vnp_Params.put("vnp_ReturnUrl", "http://localhost:3000/vnpay-return");
            vnp_Params.put("vnp_IpAddr", ipAddress);
            
            // Thời gian giao dịch
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            
            // Thời gian hết hạn giao dịch
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
            
            // Sắp xếp các tham số theo thứ tự a-z
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            
            // Tạo chuỗi hash data
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // Append để tạo chuỗi hash data
                    hashData.append(fieldName).append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    
                    // Append để tạo chuỗi query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            
            // Tạo chữ ký
            String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);
            
            // Tạo URL thanh toán
            return vnPayConfig.getVnpPayUrl() + "?" + query;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating VNPay payment URL: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponseDTO processPaymentReturn(Map<String, String> vnpParams) {
        // Lấy các tham số trả về từ VNPay
        String vnp_ResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnp_TxnRef = vnpParams.get("vnp_TxnRef");
        String vnp_Amount = vnpParams.get("vnp_Amount");
        String vnp_TransactionNo = vnpParams.get("vnp_TransactionNo");
        String vnp_BankCode = vnpParams.get("vnp_BankCode");
        String vnp_PayDate = vnpParams.get("vnp_PayDate");
        
        // Kiểm tra chữ ký
        String vnp_SecureHash = vnpParams.get("vnp_SecureHash");
        
        // Tạo chuỗi hash data để kiểm tra
        Map<String, String> validParams = new HashMap<>();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            if (!entry.getKey().equals("vnp_SecureHash") && !entry.getKey().equals("vnp_SecureHashType")) {
                validParams.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Sắp xếp các tham số theo thứ tự a-z
        List<String> fieldNames = new ArrayList<>(validParams.keySet());
        Collections.sort(fieldNames);
        
        // Tạo chuỗi hash data
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = validParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }
        
        // Tính toán chữ ký
        String calculatedHash = vnPayConfig.hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
        
        // Kiểm tra chữ ký và mã phản hồi
        boolean isValidSignature = calculatedHash.equals(vnp_SecureHash);
        boolean isSuccessful = "00".equals(vnp_ResponseCode);
        
        // Lấy orderId từ vnp_TxnRef
        Integer orderId = Integer.parseInt(vnp_TxnRef.split("_")[0]);
        
        // Cập nhật trạng thái đơn hàng nếu thanh toán thành công
        if (isValidSignature && isSuccessful) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            // Cập nhật trạng thái thanh toán
            order.setPaymentStatus("Paid");
            order.setOrderStatus(1); // Chuyển sang trạng thái Processing
            orderRepository.save(order);
        }
        
        // Tạo response
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setSuccess(isValidSignature && isSuccessful);
        response.setOrderId(orderId);
        response.setAmount(Long.parseLong(vnp_Amount) / 100); // Chuyển từ xu sang VND
        response.setTransactionId(vnp_TransactionNo);
        response.setBankCode(vnp_BankCode);
        response.setPaymentDate(vnp_PayDate);
        response.setMessage(getResponseMessage(vnp_ResponseCode));
        
        return response;
    }
    
    private String getResponseMessage(String responseCode) {
        switch (responseCode) {
            case "00":
                return "Giao dịch thành công";
            case "01":
                return "Giao dịch đã tồn tại";
            case "02":
                return "Merchant không hợp lệ";
            case "03":
                return "Dữ liệu gửi sang không đúng định dạng";
            case "04":
                return "Khởi tạo GD không thành công do Website đang bị tạm khóa";
            case "05":
                return "Giao dịch không thành công do: Quý khách nhập sai mật khẩu thanh toán quá số lần quy định";
            case "06":
                return "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch";
            case "07":
                return "Giao dịch bị nghi ngờ là giao dịch gian lận";
            case "09":
                return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking";
            case "10":
                return "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11":
                return "Giao dịch không thành công do: Đã hết hạn chờ thanh toán";
            case "24":
                return "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            default:
                return "Giao dịch thất bại";
        }
    }
}