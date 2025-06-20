package Ecommerce.BookWeb.Project.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @ManyToMany(mappedBy = "categories")
    private List<Book> books;

}
