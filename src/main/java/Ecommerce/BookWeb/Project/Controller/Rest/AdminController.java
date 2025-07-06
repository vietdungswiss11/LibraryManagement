package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.BookDTO;
import Ecommerce.BookWeb.Project.DTO.BookMapper;
import Ecommerce.BookWeb.Project.DTO.CategoryDTO;
import Ecommerce.BookWeb.Project.DTO.CategoryMapper;
import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Category;
import Ecommerce.BookWeb.Project.Model.Image;
import Ecommerce.BookWeb.Project.Model.Order;
import Ecommerce.BookWeb.Project.Repository.BookRepository;
import Ecommerce.BookWeb.Project.Repository.CategoryRepository;
import Ecommerce.BookWeb.Project.Repository.OrderRepository;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    @Autowired
    public AdminController(BookRepository bookRepository,
                           CategoryRepository categoryRepository,
                           OrderRepository orderRepository, UserRepository userRepository,
                           BookMapper bookMapper) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // CUD Book
    // Tạo sách mới
    @PostMapping("/books")
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(bookMapper.toBookDTO(savedBook));
    }

    // Cập nhật sách
    @PutMapping("/books/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable int id, @RequestBody BookDTO bookDTO) {
        return bookRepository.findById(id)
                .map(book -> {
                    // Map các trường từ DTO sang entity
                    book.setTitle(bookDTO.getTitle());
                    book.setAuthor(bookDTO.getAuthor());
                    book.setOriginalPrice(bookDTO.getOriginalPrice());
                    book.setDiscountPercent(bookDTO.getDiscountPercent());
                    book.setDiscountPrice(bookDTO.getDiscountPrice());
                    book.setDescription(bookDTO.getDescription());
                    book.setSold(bookDTO.getSold());
                    book.setPublicationDate(bookDTO.getPublicationDate());
                    if (bookDTO.getImages() != null) {
                        List<Image> images = bookDTO.getImages().stream()
                                .map(bookMapper::toImageEntity)
                                .collect(Collectors.toList());
                        for (Image img : images) {
                            img.setBook(book);
                        }
                        book.setImages(images);
                    }
                    if (bookDTO.getIsbn() != null) book.setIsbn(bookDTO.getIsbn());

                    Book updatedBook = bookRepository.save(book);
                    return ResponseEntity.ok(bookMapper.toBookDTO(updatedBook));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Xóa sách
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable int id) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //CUD Category
    // Tạo mới danh mục
    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category category) {
       return categoryRepository.save(category);
    }

    // Cập nhật danh mục
    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable int id,
            @RequestBody Category category) {

        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(category.getName());
                    Category updatedCategory = categoryRepository.save(existingCategory);
                    return ResponseEntity.ok(updatedCategory);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Xóa danh mục
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    categoryRepository.delete(category);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //UD Order
    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable int id, @RequestBody Order orderDetails) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(orderDetails.getStatus());
                    // Update other fields as needed
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id) {
        return orderRepository.findById(id)
                .map(order -> {
                    orderRepository.delete(order);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
