package Ecommerce.BookWeb.Project.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDTO {
    @NotNull(message = "User ID is required")
    private int userId;

    @NotNull(message = "Address ID is required")
    private int addressId;

    @NotNull(message = "Payment is required")
    private String paymentMethod;

    @NotNull(message = "Shipping Provider is required")
    // Thông tin giao hàng
    private String shippingProvider;

    @Valid
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequestDTO> orderItems;

    @Data
    public static class OrderItemRequestDTO {
        @NotNull(message = "Book ID is required")
        private int bookId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
    }

}
