package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.User;
import Ecommerce.BookWeb.Project.Model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByUser(User user);
    Optional<Wishlist> findByUserAndBook(User user, Book book);
    boolean existsByUserAndBook(User user, Book book);
    Long countByUser(User user);
}
