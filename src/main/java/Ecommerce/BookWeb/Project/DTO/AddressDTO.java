package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;

@Data
public class AddressDTO {
    private int id;
    private String addressLine;
    private String phoneNumber;
    private String recipientName;
    private boolean isDefault;
    // Exclude user to prevent circular reference
    // private UserDTO user; // Don't include this to avoid circular reference
}
