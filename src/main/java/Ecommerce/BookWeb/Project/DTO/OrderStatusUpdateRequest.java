package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    private String orderStatus;      // PENDING, CONFIRMED, ...
    private String paymentStatus;    // PENDING, PAID, FAILED, ...
    private String shippingStatus;   // PENDING, SHIPPED, DELIVERED, ...
    private String notes;            // (optional) ghi chú
    // getter, setter
}
