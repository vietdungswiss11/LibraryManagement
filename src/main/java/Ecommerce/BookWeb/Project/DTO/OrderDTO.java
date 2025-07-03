package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.Order;
import Ecommerce.BookWeb.Project.Model.Payment;
import Ecommerce.BookWeb.Project.Model.Shipping;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    // Thông tin đơn hàng
    private int id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private String status;
    
    // Thông tin khách hàng
    private UserDTO userDTO;
    
    // Thông tin địa chỉ giao hàng
    private AddressDTO addressDTO;
    
    // Thông tin thanh toán
    private PaymentInfoDTO paymentDTO;
    
    // Thông tin vận chuyển
    private ShippingInfoDTO shippingDTO;
    
    // Chi tiết đơn hàng
    private List<OrderItemDTO> orderItems;
    private int totalItems;
    private double subtotal;          // Tổng tiền hàng
    private double shippingFee;       // Phí vận chuyển
    //private double discountAmount;    // Tiền giảm giá
    private double totalAmount;       // Tổng thanh toán

    // Các phương thức tiện ích
//    public boolean isCancellable() {
//        return status == OrderStatus.PENDING ||
//               status == OrderStatus.PROCESSING ||
//               status == OrderStatus.AWAITING_PAYMENT;
//    }
//
//    public boolean isReturnable() {
//        return status == OrderStatus.DELIVERED &&
//               deliveredDate != null &&
//               deliveredDate.plusDays(7).isAfter(LocalDateTime.now());
//    }
}
