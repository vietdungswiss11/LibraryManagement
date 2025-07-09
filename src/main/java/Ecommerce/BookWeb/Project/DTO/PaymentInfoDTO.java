package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.Payment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentInfoDTO {
    private int id;
    private String transactionId;         // Mã giao dịch từ cổng thanh toán
    private String paymentMethod;   // Phương thức thanh toán (COD, VNPay, Momo, ...)
    private String status;          // Trạng thái thanh toán
    private double amount;             // Số tiền thanh toán
    private LocalDateTime paymentDate;     // Ngày thanh toán
    private String paymentDetails;         // Chi tiết thanh toán (JSON)
    private String paymentNote;            // Ghi chú thanh toán
    
    // Thông tin bổ sung tùy thuộc vào phương thức thanh toán
    private String bankCode;               // Mã ngân hàng (nếu có)
    private String bankTranNo;             // Mã giao dịch ngân hàng
    private String bankName;               // Tên ngân hàng (nếu có)

}
