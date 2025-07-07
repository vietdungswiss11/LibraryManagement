package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.*;
import org.springframework.stereotype.Component;

import java.util.List;
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
        dto.setOriginalPrice(book.getOriginalPrice());
        dto.setDiscountPercent(book.getDiscountPercent());
        dto.setDiscountPrice(book.getDiscountPrice()); //lấy hàm
        dto.setSold(book.getSold());
        dto.setPublicationDate(book.getPublicationDate());

        
        // Chuyển đổi reviews nếu có
        if (book.getReviews() != null) {
            dto.setReviews(book.getReviews().stream()
                    .map(this::toReviewDTO)
                    .collect(Collectors.toList()));
            
            // Tính điểm đánh giá trung bình
            double avgRating = book.getReviews().stream()
                    .mapToDouble(Review::getRating)
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


        //chuyển đổi images nếu có
        if(book.getImages() != null){
            dto.setImages(book.getImages().stream()
                    .map(this::toImageDTO)
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

    private ImageDTO toImageDTO(Image image) {
        if (image == null) {
            return null;
        }

        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setName(image.getName());
        dto.setUrl(image.getUrl());

        return dto;
    }

    public Book toEntity(BookDTO dto) {
        if (dto == null) return null;
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setOriginalPrice(dto.getOriginalPrice());
        book.setDiscountPercent(dto.getDiscountPercent());
        book.setDiscountPrice(dto.getDiscountPrice());
        book.setSold(dto.getSold());
        book.setPublicationDate(dto.getPublicationDate());
        // Map images
        if (dto.getImages() != null) {
            List<Image> images = dto.getImages().stream()
                    .map(this::toImageEntity)
                    .collect(Collectors.toList());
            for (Image img : images) {
                img.setBook(book);
            }
            book.setImages(images);
        }
        // Map categories
        if(dto.getCategories() != null){
            List<Category> categories = dto.getCategories().stream()
                    .map(this::toCategoryEntity)
                    .collect(Collectors.toList());
            book.setCategories(categories);
        }
        return book;
    }

    public Image toImageEntity(ImageDTO dto) {
        if (dto == null) return null;
        Image img = new Image();
        img.setId(dto.getId());
        img.setName(dto.getName());
        img.setUrl(dto.getUrl());
        return img;
    }

    public Category toCategoryEntity(CategoryDTO dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }
}
