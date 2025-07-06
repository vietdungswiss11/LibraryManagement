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
public interface BookRepository extends JpaRepository<Book, Integer> {
    // Các phương thức hiện có
    boolean existsByIsbn(String isbn);
    List<Book> findByIsbn(String isbn);
    
    // Tìm sách đang giảm giá (discountPercent > 0) có phân trang
    Page<Book> findByDiscountPercentGreaterThan(double discountPercent, Pageable pageable);
    
    // Tìm sách theo tác giả có phân trang
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    // Tìm sách theo tiêu đề có phân trang
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findByCategories_Id(Integer categoryId, Pageable pageable);


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
    
    @Query("SELECT b FROM Book b WHERE b.discountPrice >= :minPrice AND b.discountPrice <= :maxPrice")
    Page<Book> findByDiscountPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);

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

    // 1. Lấy sách theo multiple categories (OR logic - sách thuộc ít nhất 1 category)
    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c WHERE c.id IN :categoryIds")
    List<Book> findByCategoryIdsAsList(@Param("categoryIds") List<Integer> categoryIds);

    // 2. OPTIONAL: Lấy sách có TẤT CẢ categories được chỉ định (AND logic)
    @Query("SELECT b FROM Book b WHERE b.id IN " +
            "(SELECT b2.id FROM Book b2 JOIN b2.categories cat " +
            "WHERE cat.id IN :categoryIds " +
            "GROUP BY b2.id HAVING COUNT(DISTINCT cat.id) = :categoryCount)")
    List<Book> findBooksWithAllCategories(@Param("categoryIds") List<Integer> categoryIds,
                                        @Param("categoryCount") Long categoryCount);

    //get related books
    @Query("select distinct b from Book b join b.categories c where c.id in :categoryIds and b.id != :excludedBookId")
    List<Book> findByCategories_IdIn(@Param("categoryIds")List<Integer> categoryIds,
                                     @Param("excludedBookId")int bookId,
                                     Pageable pageable);
}