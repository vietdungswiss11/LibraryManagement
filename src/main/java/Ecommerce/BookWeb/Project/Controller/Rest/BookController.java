package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.BookDTO;
import Ecommerce.BookWeb.Project.DTO.BookMapper;
import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Category;
import Ecommerce.BookWeb.Project.Repository.BookRepository;
import Ecommerce.BookWeb.Project.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import Ecommerce.BookWeb.Project.DTO.ReviewDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private ReviewRepository reviewRepository;
    
    // Lấy danh sách sách đang giảm giá (discountPercent > 0)
    @GetMapping("/sale")
    public ResponseEntity<Map<String, Object>> getDiscountedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "discountPercent") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            // Tạo đối tượng phân trang
            Pageable paging = PageRequest.of(
                page, 
                size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
            );
            
            // Lấy danh sách sách đang giảm giá
            Page<Book> pageBooks = bookRepository.findByDiscountPercentGreaterThan(0, paging);
            
            // Chuyển đổi sang DTO
            List<BookDTO> bookDTOs = pageBooks.getContent().stream()
                .map(book -> {
                    BookDTO dto = bookMapper.toBookDTO(book);
                    // Tính toán đánh giá trung bình nếu cần
                    if (dto.getReviews() != null) {
                        double averageRating = dto.getReviews().stream()
                            .mapToDouble(ReviewDTO::getRating)
                            .average()
                            .orElse(0.0);
                        dto.setAverageRating(Math.round(averageRating * 10.0) / 10.0);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
            
            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("books", bookDTOs);
            response.put("currentPage", pageBooks.getNumber());
            response.put("totalItems", pageBooks.getTotalElements());
            response.put("totalPages", pageBooks.getTotalPages());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi lấy danh sách sách giảm giá: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ENDPOINT CHÍNH - Thay thế getAllBooks và advancedFilter
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) Integer categoryId, // Để tương thích với code cũ
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

        // Xử lý tương thích với categoryId đơn lẻ
        if (categoryId != null) {
            if (categoryIds == null) {
                categoryIds = new ArrayList<>();
            }
            if (!categoryIds.contains(categoryId)) {
                categoryIds.add(categoryId);
            }
        }

        return filterBooks(title, author, categoryIds, minPrice, maxPrice, priceRange,
                minRating, maxRating, rating, page, size, sortBy, sortDir);
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

    // CORE FILTERING METHOD
    private ResponseEntity<Map<String, Object>> filterBooks(
            String title,
            String author,
            List<Integer> categoryIds,
            Double minPrice,
            Double maxPrice,
            String priceRange,
            Double minRating,
            Double maxRating,
            String rating,
            int page,
            int size,
            String sortBy,
            String sortDir) {

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

        // Lấy danh sách sách ban đầu
        List<Book> allBooks;

        // Tối ưu: Bắt đầu với filter hiệu quả nhất
        if (categoryIds != null && !categoryIds.isEmpty()) {
            allBooks = bookRepository.findByCategoryIdsAsList(categoryIds);
        } else if (title != null) {
            // Nếu không có category filter, dùng title filter từ database
            Page<Book> titleBooks = bookRepository.findByTitleContainingIgnoreCase(title, Pageable.unpaged());
            allBooks = titleBooks.getContent();
        } else if (author != null) {
            // Tương tự với author
            Page<Book> authorBooks = bookRepository.findByAuthorContainingIgnoreCase(author, Pageable.unpaged());
            allBooks = authorBooks.getContent();
        } else if (minPrice != null || maxPrice != null) {
            // Filter price từ database
            Double finalMinPrice = minPrice != null ? minPrice : 0.0;
            Double finalMaxPrice = maxPrice != null ? maxPrice : Double.MAX_VALUE;
            Page<Book> priceBooks = bookRepository.findByDiscountPriceBetween(finalMinPrice, finalMaxPrice, Pageable.unpaged());
            allBooks = priceBooks.getContent();
        } else {
            allBooks = bookRepository.findAll();
        }

        // Áp dụng các filter còn lại
        Double finalMinPrice1 = minPrice;
        Double finalMaxPrice1 = maxPrice;
        Double finalMinRating = minRating;
        List<Book> filteredBooks = allBooks.stream()
                .filter(book -> {
                    // Filter theo title (nếu chưa filter ở database)
                    if (title != null && (categoryIds != null || finalMinPrice1 != null || finalMaxPrice1 != null)) {
                        if (!book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                            return false;
                        }
                    }

                    // Filter theo author (nếu chưa filter ở database)
                    if (author != null && (categoryIds != null || title != null || finalMinPrice1 != null || finalMaxPrice1 != null)) {
                        if (!book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                            return false;
                        }
                    }

                    // Filter theo price (nếu chưa filter ở database)
                    if ((finalMinPrice1 != null || finalMaxPrice1 != null) && (categoryIds != null || title != null || author != null)) {
                        if (finalMinPrice1 != null && book.getDiscountPrice() < finalMinPrice1) {
                            return false;
                        }
                        if (finalMaxPrice1 != null && book.getDiscountPrice() > finalMaxPrice1) {
                            return false;
                        }
                    }

                    // Filter theo rating (luôn cần filter trong Java vì phức tạp)
                    if (finalMinRating != null || maxRating != null) {
                        Double avgRating = reviewRepository.getAverageRatingByBook(book);
                        if (avgRating == null) {
                            avgRating = 0.0;
                        }

                        if (finalMinRating != null && avgRating < finalMinRating) {
                            return false;
                        }
                        if (maxRating != null && avgRating > maxRating) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        // Áp dụng sorting
        if (!"id".equals(sortBy)) {
            Comparator<Book> comparator = getBookComparator(sortBy, sortDir);
            if (comparator != null) {
                filteredBooks.sort(comparator);
            }
        }

        // Tạo pagination manually
        int start = page * size;
        int end = Math.min(start + size, filteredBooks.size());

        List<Book> pageContent = start < filteredBooks.size() ?
                filteredBooks.subList(start, end) :
                new ArrayList<>();

        Page<Book> pageBooks = new PageImpl<>(
                pageContent,
                pageable,
                filteredBooks.size()
        );

        return createPagedResponse(pageBooks);
    }

    // Helper method để tạo comparator cho sorting
    private Comparator<Book> getBookComparator(String sortBy, String sortDir) {
        Comparator<Book> comparator = null;

        switch (sortBy.toLowerCase()) {
            case "title":
                comparator = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
                break;
            case "author":
                comparator = Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);
                break;
            case "price":
                comparator = Comparator.comparing(Book::getDiscountPrice);
                break;
            case "publicationdate":
                comparator = Comparator.comparing(Book::getPublicationDate,
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "rating":
                comparator = Comparator.comparing(book -> {
                    Double rating = reviewRepository.getAverageRatingByBook(book);
                    return rating != null ? rating : 0.0;
                });
                break;
            default:
                comparator = Comparator.comparing(Book::getId);
                break;
        }

        if ("desc".equalsIgnoreCase(sortDir) && comparator != null) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable int id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    // lấy related books
    @GetMapping("/{bookId}/related")
    public ResponseEntity<List<BookDTO>> getRelatedBooks(
            @PathVariable int bookId,
            @RequestParam(defaultValue = "5") int limit) {

        //find the book by id
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        //get category Ids of the current book
        List<Integer> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        if(categoryIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        //find other books in the same categories
        List<Book> relatedBooks = bookRepository.findByCategories_IdIn(categoryIds, bookId, PageRequest.of(0, limit));

        //convert to BookDTO
        List<BookDTO> relatedBookDTOs = relatedBooks.stream()
                .map(bookMapper::toBookDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(relatedBookDTOs);
    }
}