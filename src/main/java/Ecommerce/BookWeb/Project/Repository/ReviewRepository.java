package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Review;
import Ecommerce.BookWeb.Project.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByBook(Book book);
    List<Review> findByUser(User user);
    Optional<Review> findByUserAndBook(User user, Book book);
    boolean existsByUserAndBook(User user, Book book);
    Long countByBook(Book book);
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.book = :book")
    Double getAverageRatingByBook(@Param("book") Book book);
}
