package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private int id;
    private String content;
    private double rating;
    private LocalDateTime createdAt;
    private UserDTO user;
    
    // Có thể thêm các trường bổ sung nếu cần
    private String formattedDate; // Ví dụ: "2 ngày trước"
}
