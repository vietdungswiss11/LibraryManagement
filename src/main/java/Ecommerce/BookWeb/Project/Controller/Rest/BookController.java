package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.BookDTO;
import Ecommerce.BookWeb.Project.DTO.BookMapper;
import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Repository.BookRepository;
import Ecommerce.BookWeb.Project.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*")
public class BookController {
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private ReviewRepository reviewRepository;
    // Lấy tất cả sách với phân trang và tùy chọn lọc
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) String rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDir), sortBy)
        );

        // Xử lý priceRange nếu có
        if (priceRange != null) {
            switch (priceRange.toLowerCase()) {
                case "under150k":
                    minPrice = 0.0;
                    maxPrice = 150000.0;
                    break;
                case "150k-300k":
                    minPrice = 150000.0;
                    maxPrice = 300000.0;
                    break;
                case "300k-500k":
                    minPrice = 300000.0;
                    maxPrice = 500000.0;
                    break;
                case "over500k":
                    minPrice = 500000.0;
                    maxPrice = Double.MAX_VALUE;
                    break;
            }
        }

        // Xử lý rating nếu có
        if (rating != null) {
            switch (rating.toLowerCase()) {
                case "4star":
                    minRating = 4.0;
                    break;
                case "4.5star":
                    minRating = 4.5;
                    break;
                case "5star":
                    minRating = 5.0;
                    break;
            }
        }

        Page<Book> pageBooks;

        if (title != null) {
            pageBooks = bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        }
        else if (author != null) {
            pageBooks = bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
        }
        else if (categoryId != null) {
            pageBooks = bookRepository.findByCategories_Id(categoryId, pageable);
        }
        else if (minPrice != null || maxPrice != null) {
            minPrice = minPrice != null ? minPrice : 0.0;
            maxPrice = maxPrice != null ? maxPrice : Double.MAX_VALUE;
            pageBooks = bookRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        }
        else if (minRating != null) {
            pageBooks = bookRepository.findByAverageRatingGreaterThanEqual(minRating, pageable);
        }
        else {
            pageBooks = bookRepository.findAll(pageable);
        }

        return createPagedResponse(pageBooks);
    }

    // Tìm kiếm sách theo từ khóa
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDir), sortBy)
        );

        Page<Book> pageBooks = bookRepository.searchBooks(keyword, pageable);
        return createPagedResponse(pageBooks);
    }

    // Lọc sách nâng cao
    @GetMapping("/advanced-filter")
    public ResponseEntity<Map<String, Object>> advancedFilter(
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDir), sortBy)
        );

        Page<Book> pageBooks;

        if (categoryIds != null && !categoryIds.isEmpty()) {
            pageBooks = bookRepository.findByCategoryIds(
                    categoryIds,
                    (long) categoryIds.size(),
                    pageable
            );
        } else {
            pageBooks = bookRepository.findByMultipleConditions(
                    null, // title
                    null, // author
                    minPrice,
                    maxPrice,
                    pageable
            );

            // Lọc thêm theo rating nếu có
            if (minRating != null || maxRating != null) {
                // Create final copies for use in lambda
                final Double finalMinRating = minRating != null ? minRating : 0.0;
                final Double finalMaxRating = maxRating != null ? maxRating : 5.0;

                List<Book> filteredBooks = pageBooks.getContent().stream()
                        .filter(book -> {
                            Double avgRating = reviewRepository.getAverageRatingByBook(book);
                            return avgRating != null && avgRating >= finalMinRating && avgRating <= finalMaxRating;
                        })
                        .collect(Collectors.toList());

                // Tạo lại Page từ danh sách đã lọc
                pageBooks = new PageImpl<>(filteredBooks, pageable, filteredBooks.size());
            }
        }

        return createPagedResponse(pageBooks);
    }

    // Các phương thức cũ giữ nguyên
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable int id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public BookDTO createBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookDTO(savedBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable int id, @RequestBody Book bookDetails) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(bookDetails.getTitle());
                    book.setAuthor(bookDetails.getAuthor());
                    book.setPrice(bookDetails.getPrice());
                    book.setDescription(bookDetails.getDescription());
                    book.setStockQuantity(bookDetails.getStockQuantity());
                    book.setPublicationDate(bookDetails.getPublicationDate());

                    if(bookDetails.getImages() != null) {
                        book.setImages(bookDetails.getImages());
                    }
                    if(bookDetails.getIsbn() != null) book.setIsbn(bookDetails.getIsbn());

                    Book updatedBook = bookRepository.save(book);
                    return ResponseEntity.ok(bookMapper.toBookDTO(updatedBook));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable int id) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Phương thức hỗ trợ tạo phản hồi phân trang
    private ResponseEntity<Map<String, Object>> createPagedResponse(Page<Book> pageBooks) {
        List<BookDTO> books = pageBooks.getContent().stream()
                .map(bookMapper::toBookDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("books", books);
        response.put("currentPage", pageBooks.getNumber());
        response.put("totalItems", pageBooks.getTotalElements());
        response.put("totalPages", pageBooks.getTotalPages());

        return ResponseEntity.ok(response);
    }
}