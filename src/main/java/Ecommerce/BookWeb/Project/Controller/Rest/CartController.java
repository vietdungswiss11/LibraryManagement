package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.Model.Cart;
import Ecommerce.BookWeb.Project.Model.User;
import Ecommerce.BookWeb.Project.Repository.CartRepository;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/cart")
public class CartController {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartController(CartRepository cartRepository, 
                         UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get user's cart
     * @param userId ID of the user
     * @return Cart information or 404 if user not found
     */
    @GetMapping
    public ResponseEntity<?> getCart(@PathVariable int userId) {
        return userRepository.findById(userId)
                .map(user -> cartRepository.findByUser(user)
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> {
                            // Create new cart if not exists
                            Cart newCart = new Cart();
                            newCart.setUser(user);
                            return ResponseEntity.ok(cartRepository.save(newCart));
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Clear all items from cart
     * @param userId ID of the user
     * @return 200 OK if successful, 404 if user not found
     */
    @DeleteMapping
    public ResponseEntity<?> clearCart(@PathVariable int userId) {
        return userRepository.findById(userId)
                .map(user -> cartRepository.findByUser(user)
                        .map(cart -> {
                            cart.getCartItems().clear();
                            cartRepository.save(cart);
                            return ResponseEntity.ok().build();
                        })
                        .orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
    }
}
