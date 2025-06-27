package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Category;
import Ecommerce.BookWeb.Project.Model.Review;
import Ecommerce.BookWeb.Project.Model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BookMapper {
    
    public BookDTO toBookDTO(Book book) {
        if (book == null) {
            return null;
        }
        
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDescription(book.getDescription());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice());
        dto.setStockQuantity(book.getStockQuantity());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setImages(book.getImages());
        
        // Chuyển đổi reviews nếu có
        if (book.getReviews() != null) {
            dto.setReviews(book.getReviews().stream()
                    .map(this::toReviewDTO)
                    .collect(Collectors.toList()));
            
            // Tính điểm đánh giá trung bình
            double avgRating = book.getReviews().stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            dto.setAverageRating(Math.round(avgRating * 10.0) / 10.0); // Làm tròn 1 chữ số thập phân
            dto.setTotalReviews(book.getReviews().size());
        }
        
        // Chuyển đổi categories nếu có
        if (book.getCategories() != null) {
            dto.setCategories(book.getCategories().stream()
                    .map(this::toCategoryDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private ReviewDTO toReviewDTO(Review review) {
        if (review == null) {
            return null;
        }
        
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        
        // Chuyển đổi user nếu có
        if (review.getUser() != null) {
            dto.setUser(toUserDTO(review.getUser()));
        }
        
        return dto;
    }
    
    private UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        
        return dto;
    }
    
    private CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        
        return dto;
    }
}
