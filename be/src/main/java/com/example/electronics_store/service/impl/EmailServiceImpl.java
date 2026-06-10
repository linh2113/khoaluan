package com.example.electronics_store.service.impl;

import com.example.electronics_store.model.Order;
import com.example.electronics_store.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@example.com}")
    private String fromEmail;

    @Override
public void sendPaymentSuccessEmail(Order order) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(order.getUser().getEmail());
        helper.setSubject("Thanh toán thành công - Đơn hàng #" + order.getId());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String subtotal = formatter.format(order.getTotalPrice());
        String shippingFee = formatter.format(order.getShippingFee());
        String totalAmount = formatter.format(order.getTotalPrice() + order.getShippingFee());

        // Tạo danh sách sản phẩm với hình ảnh
        StringBuilder productList = new StringBuilder();
        order.getOrderDetails().forEach(detail -> {
            String productPrice = formatter.format(detail.getPrice());
            String productImage = detail.getProduct().getImage() != null ? detail.getProduct().getImage() : "";
            productList.append("<tr>")
                    .append("<td style='padding: 8px;'>")
                    .append("<img src='").append(productImage).append("' style='width: 50px; height: 50px; object-fit: cover; margin-right: 10px;' />")
                    .append(detail.getProduct().getName())
                    .append("</td>")
                    .append("<td style='padding: 8px;'>").append(detail.getQuantity()).append("</td>")
                    .append("<td style='padding: 8px;'>").append(productPrice).append("</td>")
                    .append("<td style='padding: 8px;'>").append(formatter.format(detail.getPrice() * detail.getQuantity())).append("</td>")
                    .append("</tr>");
        });

        String emailContent = "<html><body>" +
                "<h2>Thanh toán thành công!</h2>" +
                "<p>Xin chào " + order.getUser().getUserName() + ",</p>" +
                "<p>Chúng tôi xác nhận rằng bạn đã thanh toán thành công cho đơn hàng:</p>" +
                "<div style='border: 1px solid #ddd; padding: 15px; margin: 10px 0;'>" +
                "<h3>Thông tin đơn hàng</h3>" +
                "<p><strong>Mã đơn hàng:</strong> #" + order.getId() + "</p>" +
                "<p><strong>Tổng tiền sản phẩm:</strong> " + subtotal + "</p>" +
                "<p><strong>Phí vận chuyển:</strong> " + shippingFee + "</p>" +
                "<p><strong>Tổng thanh toán:</strong> " + totalAmount + "</p>" +
                "<p><strong>Phương thức thanh toán:</strong> " + order.getPaymentMethod().getMethodName() + "</p>" +
                "<p><strong>Trạng thái:</strong> " + order.getPaymentStatus() + "</p>" +
                "</div>" +
                "<div style='border: 1px solid #ddd; padding: 15px; margin: 10px 0;'>" +
                "<h3>Sản phẩm đã mua</h3>" +
                "<table style='width: 100%; border-collapse: collapse;'>" +
                "<tr style='background-color: #f2f2f2;'>" +
                "<th style='border: 1px solid #ddd; padding: 8px;'>Sản phẩm</th>" +
                "<th style='border: 1px solid #ddd; padding: 8px;'>Số lượng</th>" +
                "<th style='border: 1px solid #ddd; padding: 8px;'>Đơn giá</th>" +
                "<th style='border: 1px solid #ddd; padding: 8px;'>Thành tiền</th>" +
                "</tr>" +
                productList.toString() +
                "</table>" +
                "</div>" +
                "<p>Đơn hàng của bạn đang được xử lý và sẽ được giao sớm nhất có thể.</p>" +
                "<p>Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi!</p>" +
                "</body></html>";

        helper.setText(emailContent, true);
        mailSender.send(message);

    } catch (Exception e) {
        System.err.println("Failed to send payment success email: " + e.getMessage());
    }
}

@Override
public void sendOrderConfirmationEmail(Order order) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(order.getUser().getEmail());
        helper.setSubject("Xác nhận đơn hàng #" + order.getId());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String subtotal = formatter.format(order.getTotalPrice());
        String shippingFee = formatter.format(order.getShippingFee());
        String totalAmount = formatter.format(order.getTotalPrice() + order.getShippingFee());

        // Tạo danh sách sản phẩm với hình ảnh
        StringBuilder productList = new StringBuilder();
        order.getOrderDetails().forEach(detail -> {
            String productPrice = formatter.format(detail.getPrice());
            String productImage = detail.getProduct().getImage() != null ? detail.getProduct().getImage() : "";
            productList.append("<tr>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd;'>")
                    .append("<img src='").append(productImage).append("' style='width: 50px; height: 50px; object-fit: cover; margin-right: 10px; vertical-align: middle;' />")
                    .append("<span style='vertical-align: middle;'>").append(detail.getProduct().getName()).append("</span>")
                    .append("</td>")
                    .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(detail.getQuantity()).append("</td>")
                    .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(productPrice).append("</td>")
                    .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(formatter.format(detail.getPrice() * detail.getQuantity())).append("</td>")
                    .append("</tr>");
        });

        String emailContent = "<html><body>" +
                "<h2>Đặt hàng thành công!</h2>" +
                "<p>Xin chào " + order.getUser().getUserName() + ",</p>" +
                "<p>Cảm ơn bạn đã đặt hàng tại cửa hàng của chúng tôi. Đây là thông tin đơn hàng của bạn:</p>" +
                "<div style='border: 1px solid #ddd; padding: 15px; margin: 10px 0;'>" +
                "<h3>Thông tin đơn hàng</h3>" +
                "<p><strong>Mã đơn hàng:</strong> #" + order.getId() + "</p>" +
                "<p><strong>Tổng tiền sản phẩm:</strong> " + subtotal + "</p>" +
                "<p><strong>Phí vận chuyển:</strong> " + shippingFee + "</p>" +
                "<p><strong>Tổng thanh toán:</strong> " + totalAmount + "</p>" +
                "<p><strong>Địa chỉ giao hàng:</strong> " + order.getAddress() + "</p>" +
                "<p><strong>Số điện thoại:</strong> " + order.getPhoneNumber() + "</p>" +
                "<p><strong>Phương thức vận chuyển:</strong> " + order.getShippingMethod().getMethodName() + "</p>" +
                "<p><strong>Phương thức thanh toán:</strong> " + order.getPaymentMethod().getMethodName() + "</p>" +
                "</div>" +
                "<div style='border: 1px solid #ddd; padding: 15px; margin: 10px 0;'>" +
                "<h3>Sản phẩm đã đặt</h3>" +
                "<table style='width: 100%; border-collapse: collapse;'>" +
                "<tr style='background-color: #f2f2f2;'>" +
                "<th style='border: 1px solid #ddd; padding: 8px;'>Sản phẩm</th>" +
                "<th style='border: 1px solid #ddd; padding: 8px;'>Số lượng</th>" +
                "<th style='border: 1px solid #ddd; padding: 8px;'>Đơn giá</th>" +
                "<th style='border: 1px solid #ddd; padding: 8px;'>Thành tiền</th>" +
                "</tr>" +
                productList.toString() +
                "</table>" +
                "<p><strong>Phí vận chuyển:</strong> " + formatter.format(order.getShippingFee()) + "</p>" +
                "<p><strong>Tổng tiền:</strong> " + totalAmount + "</p>" +
                "</div>" +
                "<p>Chúng tôi sẽ xử lý đơn hàng của bạn trong thời gian sớm nhất.</p>" +
                "<p>Cảm ơn bạn đã tin tưởng và mua sắm tại cửa hàng!</p>" +
                "</body></html>";

        helper.setText(emailContent, true);
        mailSender.send(message);

    } catch (Exception e) {
        System.err.println("Failed to send order confirmation email: " + e.getMessage());
    }
}
}