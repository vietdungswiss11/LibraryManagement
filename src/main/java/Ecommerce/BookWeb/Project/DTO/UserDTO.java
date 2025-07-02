package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private int id;
    private String name;
    private String email;
    private String phoneNumber;
    private List<RoleDTO> roles;
    private List<AddressDTO> addresses;
    private List<ReviewDTO> reviews;
    //private List<OrderDTO> orders;
    // Exclude sensitive information like password and circular references
}
