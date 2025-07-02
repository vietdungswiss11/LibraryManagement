package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.CartDTO;
import Ecommerce.BookWeb.Project.DTO.CartMapper;
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
    private final CartMapper cartMapper;

    @Autowired
    public CartController(CartRepository cartRepository, 
                         UserRepository userRepository,
                         CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
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
                        .map(cart -> ResponseEntity.ok(cartMapper.toCartDTO(cart)))
                        .orElseGet(() -> {
                            // Create new cart if not exists
                            Cart newCart = new Cart();
                            newCart.setUser(user);
                            Cart savedCart = cartRepository.save(newCart);
                            return ResponseEntity.ok(cartMapper.toCartDTO(savedCart));
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
