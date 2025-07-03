package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private int id;
    private BookDTO book;
    private int quantity;
    private double price; // Giá tại thời điểm đặt hàng
    private String note;
    
    // Tính tổng tiền cho mỗi mục đơn hàng
    public double getTotalPrice() {
        return price*quantity;
    }
}
