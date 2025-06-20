package Ecommerce.BookWeb.Project.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wishlists")
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "nguoiDungId", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "sachId", nullable = false)
    private Book book;
}
