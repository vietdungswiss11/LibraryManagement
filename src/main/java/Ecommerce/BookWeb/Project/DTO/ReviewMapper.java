package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Review;
import Ecommerce.BookWeb.Project.Model.User;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    public ReviewDTO toReviewDTO(Review review){
        if(review == null){
            return null;
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());
        dto.setCreatedAt(review.getCreatedAt());

        //chuyển đổi user nếu có
        if(review.getUser() != null){
            dto.setUser(toUserDTO(review.getUser()));
        }

        return dto;
    }

    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());

        return dto;
    }

    public ReviewDTO toReviewDTOofUser(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());
        dto.setCreatedAt(review.getCreatedAt());

        //chuyển đổi book nếu có
        if(review.getBook() != null){
            dto.setBook(toBookDTO(review.getBook()));
        }
        return dto;
    }

    public BookDTO toBookDTO(Book book) {
        if (book == null) {
            return null;
        }

        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setImageUrl(book.getImages().get(0).getUrl());
        return dto;
    }

}
