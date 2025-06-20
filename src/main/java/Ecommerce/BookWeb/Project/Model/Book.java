package Ecommerce.BookWeb.Project.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "author")
    private String author;
    
    @Column(name = "isbn", unique = true)
    private String isbn;
    
    @Column(name = "price", nullable = false)
    private Long price;
    
    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;
    
    @Column(name = "publication_date")
    private LocalDate publicationDate;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Image> images;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Review> reviews;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Wishlist> wishlists;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;
    
    @OneToMany(mappedBy = "book", cascade = {
        CascadeType.PERSIST, CascadeType.MERGE,
        CascadeType.DETACH, CascadeType.REFRESH
    })
    private List<OrderItem> orderItems;
    
    @ManyToMany
    @JoinTable(
        name = "books_categories",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}
