package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.*;
import Ecommerce.BookWeb.Project.JWT.JwtTokenUtil;
import Ecommerce.BookWeb.Project.Model.Role;
import Ecommerce.BookWeb.Project.Model.User;
import Ecommerce.BookWeb.Project.Repository.RoleRepository;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtTokenUtil jwtUtils;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User userPrincipal = (User) authentication.getPrincipal();
            // Cập nhật lastLoginAt
            userPrincipal.setLastLoginAt(LocalDateTime.now());
            userRepository.save(userPrincipal);

            String jwt = jwtUtils.generateToken(userPrincipal);
            String refreshToken = jwtUtils.generateRefreshToken(userPrincipal);

            List<String> roles = userPrincipal.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt, refreshToken,
                    userPrincipal.getId(),
                    userPrincipal.getName(),
                    userPrincipal.getEmail(),
                    roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid email or password!"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error: Email is already taken!"));
        }

        // Create new user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());

        // Set default role
        List<Role> roles = new ArrayList<>();
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully!"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        if (jwtUtils.validateToken(requestRefreshToken)) {
            String email = jwtUtils.getUsernameFromToken(requestRefreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtils.generateToken(user);
            return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Refresh token is not valid!"));
        }
    }

    //Logic forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email không tồn tại!");
        }
        User user = userOpt.get();

        // Tạo mật khẩu mới ngẫu nhiên
        String newPassword = generateRandomPassword(6);

        // Mã hóa và lưu lại
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        // Gửi email
        sendNewPasswordEmail(user.getEmail(), newPassword);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Mật khẩu mới đã được gửi về email của bạn!"
        ));
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendNewPasswordEmail(String to, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mật khẩu mới cho tài khoản BookWeb");
        message.setText("Mật khẩu mới của bạn là: " + newPassword + "\nHãy đăng nhập và đổi lại mật khẩu ngay!");
        mailSender.send(message);
    }

    //update password
    @PutMapping("/users/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable int id, @Valid @RequestBody ChangePasswordRequest reqPass) {
        User user = userRepository.findById(id).orElse(null);
        if(user==null) return ResponseEntity.badRequest().body(new ApiResponse(
                false, "Người dùng không tồn tại !")
        );

        //check old password
        if(!encoder.matches(reqPass.getOldPassword(), user.getPassword())) return ResponseEntity.badRequest().body(new ApiResponse(
                false, "password cũ nhập sai!")
        );
        //check confirm password
        if(!reqPass.getNewPassword().equals(reqPass.getConfirmPassword())) return ResponseEntity.badRequest().body(new ApiResponse(
                false, "Nhâ lại mật khẩu mới sai !")
        );

        user.setPassword(encoder.encode(reqPass.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "Đã lưu password mới thành công!"));

    }

    //google login
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("idToken");
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList("93263629507-684ul8eu0bctqj7qsufh9oeipi4n67gk.apps.googleusercontent.com")) // Thay bằng clientId google console
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid Google ID token!"));
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // Kiểm tra user đã tồn tại chưa
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                // Tạo user mới
                user = new User();
                user.setEmail(email);
                user.setName(name != null ? name : "Google User");
                user.setPhoneNumber(generateRandomPhoneNumber());
                user.setCreatedAt(LocalDateTime.now());
                // Set default role
                List<Role> roles = new ArrayList<>();
                Role userRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
                user.setRoles(roles);
                // Không cần password
                user.setPassword(encoder.encode(UUID.randomUUID().toString()));
                userRepository.save(user);
            }

            // Tạo JWT
            String jwt = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(user);
            List<String> roles = user.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt, refreshToken,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    roles));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid Google ID token!"));
        }
    }

    private String generateRandomPhoneNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("09");
        for (int i = 0; i < 8; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
