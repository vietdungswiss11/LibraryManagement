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

    @Column(name = "original_price", nullable = false)
    private double originalPrice;

    @Column(name = "discount_percent")
    private double discountPercent; // Lưu dạng số thập phân, ví dụ: 0.2 cho 20%

    @Column(name= "discount_price")
    private double discountPrice;
    
    @Column(name = "sold", nullable = false)
    private int sold;
    
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

//    public double getDiscountPrice(){
//        if(discountPercent > 0){
//            discountPrice =  originalPrice * (1 - discountPercent);
//            return discountPrice;
//        }
//        return originalPrice;
//    }


}
