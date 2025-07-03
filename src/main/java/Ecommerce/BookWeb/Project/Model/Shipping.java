package Ecommerce.BookWeb.Project.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "shippings")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @OneToMany(mappedBy = "shipping")
    private List<Order> orders;

    @Column(name = "shipping_code")
    private String shippingCode;
    
    @Column(name = "status", nullable = false)
    private String status; // e.g., PENDING, PROCESSING, SHIPPED, DELIVERED
    
    @Column(name = "shipping_cost", nullable = false)
    private int shippingCost;
    
    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;
    
    @Column(name = "actual_delivery")
    private LocalDateTime actualDelivery;
    
    @Column(name = "shipping_provider")
    private String shippingProvider; // e.g., GHTK, VNPOST, GHN
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    // Address information (duplicated from order for historical purposes)
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "address_line", nullable = false, columnDefinition = "TEXT")
    private String addressLine;
}
