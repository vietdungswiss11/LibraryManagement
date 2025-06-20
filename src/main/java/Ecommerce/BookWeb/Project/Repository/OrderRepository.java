package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Order;
import Ecommerce.BookWeb.Project.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    Page<Order> findByUser(User user, Pageable pageable);
    Long countByUser(User user);
}
