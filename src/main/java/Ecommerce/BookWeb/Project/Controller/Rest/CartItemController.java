package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.Model.*;
import Ecommerce.BookWeb.Project.Repository.BookRepository;
import Ecommerce.BookWeb.Project.Repository.CartItemRepository;
import Ecommerce.BookWeb.Project.Repository.CartRepository;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/cart/items")
public class CartItemController {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public CartItemController(CartItemRepository cartItemRepository,
                            CartRepository cartRepository,
                            UserRepository userRepository,
                            BookRepository bookRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    //add item to cart
    @PostMapping("/{itemId}")
    public ResponseEntity<?> addToCart(
            @PathVariable int userId,
            @RequestParam int itemId,
            @RequestParam(defaultValue = "1") int quantity) {

        if (quantity <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be greater than 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Book book = bookRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Find or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        // Find existing cart item or create new one
        CartItem item = cartItemRepository.findByCartAndBook(cart, book)
                .map(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    return cartItemRepository.save(existingItem);
                })
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setBook(book);
                    newItem.setQuantity(quantity);
                    return cartItemRepository.save(newItem);
                });

        // Update cart's updatedAt timestamp
        cartRepository.save(cart);

        return ResponseEntity.ok(item);
    }

    @PutMapping("{itemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable int itemId,
            @RequestParam int quantity) {
        
        if (quantity <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be greater than 0");
        }
        
        return cartItemRepository.findById(itemId)
                .map(item -> {
                    item.setQuantity(quantity);
                    CartItem updatedItem = cartItemRepository.save(item);
                    // Update cart's updatedAt timestamp
                    cartRepository.save(item.getCart());
                    return ResponseEntity.ok(updatedItem);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable int itemId) {
        return cartItemRepository.findById(itemId)
                .map(item -> {
                    Cart cart = item.getCart();
                    cartItemRepository.delete(item);
                    // Update cart's updatedAt timestamp
                    cartRepository.save(cart);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
