package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;

@Data
public class CartItemDTO {
    private int id;
    private int quantity;
    private BookDTO book;
    
    // Không bao gồm tham chiếu ngược lại Cart để tránh circular reference
}
