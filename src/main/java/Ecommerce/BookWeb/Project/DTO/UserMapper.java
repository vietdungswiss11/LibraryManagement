package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        
        // Map roles using RoleMapper
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(this::toRoleDTO)
                    .collect(Collectors.toList()));
        }

        if(user.getAddresses() != null) {
            dto.setAddresses(user.getAddresses().stream()
                    .map(this::toAddressDTO)
                    .collect(Collectors.toList()));
        }

        if(user.getReviews() != null) {
            dto.setReviews(user.getReviews().stream()
                    .map(this::toReviewDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public RoleDTO toRoleDTO(Role role) {
        if (role == null) {
            return null;
        }

        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }

    public AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setAddressLine(address.getAddressLine());
        dto.setPhoneNumber(address.getPhoneNumber());
        dto.setRecipientName(address.getRecipientName());
        dto.setDefault(address.isDefault());
        return dto;
    }

    public ReviewDTO toReviewDTO(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());
        if(review.getBook() != null) {
            dto.setBook(toBookDTO(review.getBook()));
        }
        return dto;
    }

    public BookDTO toBookDTO(Book book){
        if (book == null) {
            return null;
        }

        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        return dto;
    }

    public UserDTO toUserListDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        if(user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(this::toRoleDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
