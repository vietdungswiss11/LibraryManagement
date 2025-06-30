package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private int id;
    private String name;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, String refreshToken, int id,
                       String name, String email, List<String> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }
}
