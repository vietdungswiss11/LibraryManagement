package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    // Các phương thức hiện có
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByIsbn(String isbn);
    Page<Book> findByCategories_Id(Integer categoryId, Pageable pageable);
    boolean existsByIsbn(String isbn);

    // Thêm các phương thức mới

    // Tìm kiếm sách theo từ khóa (tiêu đề, tác giả, mô tả)
    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN b.categories c " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    // Tìm sách theo khoảng giá
    Page<Book> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    // Tìm sách có giá nhỏ hơn hoặc bằng
    Page<Book> findByPriceLessThanEqual(Double maxPrice, Pageable pageable);

    // Tìm sách có giá lớn hơn hoặc bằng
    Page<Book> findByPriceGreaterThanEqual(Double minPrice, Pageable pageable);

    // Tìm sách có đánh giá trung bình >= minRating
    @Query("SELECT b FROM Book b LEFT JOIN b.reviews r " +
            "GROUP BY b " +
            "HAVING COALESCE(AVG(r.rating), 0) >= :minRating")
    Page<Book> findByAverageRatingGreaterThanEqual(
            @Param("minRating") Double minRating,
            Pageable pageable
    );

    // Tìm sách có đánh giá trung bình trong khoảng
    @Query("SELECT b FROM Book b LEFT JOIN b.reviews r " +
            "GROUP BY b " +
            "HAVING COALESCE(AVG(r.rating), 0) BETWEEN :minRating AND :maxRating")
    Page<Book> findByAverageRatingBetween(
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating,
            Pageable pageable
    );

    // Tìm sách kết hợp nhiều danh mục
    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN b.categories c " +
            "WHERE c.id IN :categoryIds " +
            "GROUP BY b " +
            "HAVING COUNT(DISTINCT c.id) = :categoryCount")
    Page<Book> findByCategoryIds(
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("categoryCount") long categoryCount,
            Pageable pageable
    );

    // Tìm sách theo tác giả có phân trang
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    // Tìm sách theo tiêu đề có phân trang
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Tìm sách theo nhiều điều kiện
    @Query("SELECT b FROM Book b " +
            "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.price <= :maxPrice)")
    Page<Book> findByMultipleConditions(
            @Param("title") String title,
            @Param("author") String author,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );
}