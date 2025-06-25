package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.Model.User;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok) // Bước 2: Nếu tìm thấy, trả về 200 OK với user
                .orElse(ResponseEntity.notFound().build());// Bước 3: Nếu không tìm thấy, trả về 404 Not Found
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    // Cập nhật các trường cơ bản
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    user.setPhoneNumber(userDetails.getPhoneNumber());
                    
                    // Cập nhật password nếu được cung cấp
                    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                        user.setPassword(userDetails.getPassword());
                    }
                    
                    // Cập nhật roles nếu được cung cấp
                    if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
                        user.setRoles(userDetails.getRoles());
                    }
                    
                    // Lưu và trả về user đã cập nhật
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
