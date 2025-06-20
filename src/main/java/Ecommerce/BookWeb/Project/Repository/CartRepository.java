package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Cart;
import Ecommerce.BookWeb.Project.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
    boolean existsByUser(User user);
}
