package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.UserDTO;
import Ecommerce.BookWeb.Project.DTO.UserMapper;
import Ecommerce.BookWeb.Project.Model.User;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository UserRepository;
    private UserMapper  userMapper;

    @Autowired
    public UserController(UserRepository UserRepository, UserMapper userMapper) {
        this.UserRepository = UserRepository;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return UserRepository.findAll().stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
        return UserRepository.findById(id)
                .map(userMapper::toUserDTO)
                .map(ResponseEntity::ok) // Bước 2: Nếu tìm thấy, trả về 200 OK với User
                .orElse(ResponseEntity.notFound().build());// Bước 3: Nếu không tìm thấy, trả về 404 Not Found
    }

    @PostMapping
    public User createUser(@RequestBody User User) {
        return UserRepository.save(User);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User UserDetails) {
        return UserRepository.findById(id)
                .map(User -> {
                    // Cập nhật các trường cơ bản
                    User.setName(UserDetails.getName());
                    User.setEmail(UserDetails.getEmail());
                    User.setPhoneNumber(UserDetails.getPhoneNumber());
                    
                    // Cập nhật password nếu được cung cấp
                    if (UserDetails.getPassword() != null && !UserDetails.getPassword().isEmpty()) {
                        User.setPassword(UserDetails.getPassword());
                    }
                    
                    // Cập nhật roles nếu được cung cấp
                    if (UserDetails.getRoles() != null && !UserDetails.getRoles().isEmpty()) {
                        User.setRoles(UserDetails.getRoles());
                    }
                    
                    // Lưu và trả về User đã cập nhật
                    User updatedUser = UserRepository.save(User);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        return UserRepository.findById(id)
                .map(User -> {
                    UserRepository.delete(User);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
