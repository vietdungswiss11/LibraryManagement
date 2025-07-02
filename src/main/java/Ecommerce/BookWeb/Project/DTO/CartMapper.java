package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.Cart;
import Ecommerce.BookWeb.Project.Model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    
    private final BookMapper bookMapper;
    
    public CartMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }
    
    public CartDTO toCartDTO(Cart cart) {
        if (cart == null) {
            return null;
        }
        
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        
        // Chuyển đổi danh sách CartItem sang CartItemDTO
        if (cart.getCartItems() != null) {
            List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                    .map(this::toCartItemDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
            
            // Tính tổng tiền
            double total = itemDTOs.stream()
                    .mapToDouble(item -> item.getBook().getDiscountPrice() * item.getQuantity())
                    .sum();
            dto.setTotalPrice(total);
        }
        
        return dto;
    }
    
    public CartItemDTO toCartItemDTO(CartItem item) {
        if (item == null) {
            return null;
        }
        
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        
        // Sử dụng BookMapper để chuyển đổi Book sang BookDTO
        if (item.getBook() != null) {
            dto.setBook(bookMapper.toBookDTO(item.getBook()));
        }
        
        return dto;
    }
}
