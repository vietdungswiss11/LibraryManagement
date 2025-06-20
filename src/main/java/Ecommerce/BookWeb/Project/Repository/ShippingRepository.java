package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Order;
import Ecommerce.BookWeb.Project.Model.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Integer> {
    Optional<Shipping> findByTrackingNumber(String trackingNumber);
    Optional<Shipping> findByOrder(Order order);
}
