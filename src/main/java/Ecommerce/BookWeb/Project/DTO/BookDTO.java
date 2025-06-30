package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.Category;
import Ecommerce.BookWeb.Project.Model.Image;
import Ecommerce.BookWeb.Project.Model.Review;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookDTO {
    private int id;
    private String title;
    private String description;
    private String author;
    private String isbn;
    private double originalPrice;
    private double discountPercent;
    private double discountPrice;
    private int sold;
    private LocalDate publicationDate;
    private List<ImageDTO> images;
    private List<ReviewDTO> reviews;
    private List<CategoryDTO> categories;
    
    // Các trường tính toán hoặc định dạng
    private double averageRating;
    private int totalReviews;
    
    // Có thể thêm các phương thức tĩnh để chuyển đổi từ Entity sang DTO
}
