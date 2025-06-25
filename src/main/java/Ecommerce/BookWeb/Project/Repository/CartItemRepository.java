package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Cart;
import Ecommerce.BookWeb.Project.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartAndBook(Cart cart, Book book);
    void deleteByCart(Cart cart);
}
