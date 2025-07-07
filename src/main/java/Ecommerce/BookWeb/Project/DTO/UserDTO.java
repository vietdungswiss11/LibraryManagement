package Ecommerce.BookWeb.Project.DTO;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private int id;
    private String name;
    private String email;
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;
    private  LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private List<RoleDTO> roles;
    private List<AddressDTO> addresses;
    private List<ReviewDTO> reviews;
    //private List<OrderDTO> orders;
    // Exclude sensitive information like password and circular references
}
