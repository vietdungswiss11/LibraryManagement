package Ecommerce.BookWeb.Project.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @OneToMany(mappedBy = "payment")
    private List<Order> orders;
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // e.g., CREDIT_CARD, PAYPAL, COD
    
    @Column(name = "amount", nullable = false)
    private Long amount;
    
    @Column(name = "status", nullable = false)
    private String status; // e.g., PENDING, COMPLETED, FAILED, REFUNDED
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    // Additional payment details can be added here
    @Column(name = "card_last_four")
    private String cardLastFour;
    
    @Column(name = "payment_gateway")
    private String paymentGateway; // e.g., STRIPE, PAYPAL, VNPAY
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
