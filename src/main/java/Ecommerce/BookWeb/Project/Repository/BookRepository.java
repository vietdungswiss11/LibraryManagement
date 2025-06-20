package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByIsbn(String isbn);
    Page<Book> findByCategories_Id(Integer categoryId, Pageable pageable);
    boolean existsByIsbn(String isbn);
}
