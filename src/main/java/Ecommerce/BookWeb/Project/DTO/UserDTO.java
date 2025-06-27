package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private int id;
    private String name;
    private String email;
    private String avatar;
    // Chỉ bao gồm các thông tin cần thiết, không bao gồm mật khẩu hoặc thông tin nhạy cảm
}
