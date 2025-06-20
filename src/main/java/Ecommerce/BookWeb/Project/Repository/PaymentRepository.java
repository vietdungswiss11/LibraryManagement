package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Order;
import Ecommerce.BookWeb.Project.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByOrder(Order order);
}
