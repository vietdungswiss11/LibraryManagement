package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;
import java.util.List;

@Data
public class CartDTO {
    private int id;
    private List<CartItemDTO> items;
    private double totalPrice; // Có thể thêm trường tính toán tổng tiền
    
    // Không bao gồm tham chiếu đến User để tránh circular reference
}
