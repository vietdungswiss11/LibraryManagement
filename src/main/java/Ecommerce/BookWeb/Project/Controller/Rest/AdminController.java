package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.*;
import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Category;
import Ecommerce.BookWeb.Project.Model.Image;
import Ecommerce.BookWeb.Project.Model.Order;
import Ecommerce.BookWeb.Project.Repository.BookRepository;
import Ecommerce.BookWeb.Project.Repository.CategoryRepository;
import Ecommerce.BookWeb.Project.Repository.OrderRepository;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    @Autowired
    public AdminController(BookRepository bookRepository,
                           CategoryRepository categoryRepository,
                           OrderRepository orderRepository, UserRepository userRepository,
                           BookMapper bookMapper, UserMapper userMapper, OrderMapper orderMapper) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
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
                    if(bookDTO.getCategories()!= null){
                        List<Category> categorires = bookDTO.getCategories().stream()
                                .map(bookMapper::toCategoryEntity)
                                .collect(Collectors.toList());
                        book.setCategories(categorires);
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
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            // Tạo đối tượng phân trang
            Pageable paging = PageRequest.of(
                    page,
                    size,
                    sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
            );

            // Lấy danh sách đơn hàng có phân trang
            Page<Order> pageOrders = orderRepository.findAll(paging);

            // Chuyển đổi sang DTO
            List<OrderDTO> orderDTOs = pageOrders.getContent().stream()
                    .map(orderMapper::toOrderDTO)
                    .collect(Collectors.toList());

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orderDTOs);
            response.put("currentPage", pageOrders.getNumber());
            response.put("totalItems", pageOrders.getTotalElements());
            response.put("totalPages", pageOrders.getTotalPages());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderDTO> updateStatusOrder(@PathVariable int id, @RequestBody OrderStatusUpdateRequest req) {
        return orderRepository.findById(id)
                .map(order -> {
                    if(order.getPayment()!= null && req.getPaymentStatus() != null){
                        order.getPayment().setStatus(req.getPaymentStatus());
                    }
                    if(order.getShipping()!=null && req.getShippingStatus() != null) {
                        order.getShipping().setStatus(req.getShippingStatus());

                    }
                    if(req.getOrderStatus() != null){
                        order.setStatus(req.getOrderStatus());
                    }
                    if(req.getNotes() !=null) order.setNotes(req.getNotes());
                    Order updateOrder = orderRepository.save(order);
                    return ResponseEntity.ok(orderMapper.toOrderDTO(updateOrder));
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

    //get only info User
    @GetMapping("/users")
    public List<UserDTO> getListUser(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserListDTO)
                .collect(Collectors.toList());
    }

    //delete User
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
