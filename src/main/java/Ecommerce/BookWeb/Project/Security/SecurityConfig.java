package Ecommerce.BookWeb.Project.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Tắt CSRF
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // Cho phép tất cả các request
            );
            
        return http.build();
    }
    
    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        // Chỉ sử dụng cho mục đích phát triển
        // Cho phép sử dụng mật khẩu đơn giản như "123456"
        return NoOpPasswordEncoder.getInstance();
    }
}