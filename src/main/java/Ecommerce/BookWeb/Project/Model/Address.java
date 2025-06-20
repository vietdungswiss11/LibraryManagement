package Ecommerce.BookWeb.Project.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "address_line", nullable = false)
    private String addressLine;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;
    
    @Column(name = "is_default", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDefault;
    
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private List<Order> orders;
}
