package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.ReviewDTO;
import Ecommerce.BookWeb.Project.DTO.ReviewMapper;
import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Review;
import Ecommerce.BookWeb.Project.Model.User;
import Ecommerce.BookWeb.Project.Repository.BookRepository;
import Ecommerce.BookWeb.Project.Repository.ReviewRepository;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private ReviewRepository reviewRepository;
    private BookRepository bookRepository;
    private ReviewMapper reviewMapper;
    private UserRepository userRepository;

    @Autowired
    ReviewController(ReviewRepository reviewRepository, BookRepository bookRepository,
                     ReviewMapper reviewMapper, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.reviewMapper = reviewMapper;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable int id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toReviewDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/book/{bookId}")
    public List<ReviewDTO> getReviewsByBookId(@PathVariable int bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if(book == null) return null;

        List<ReviewDTO> reDTOs = book.getReviews().stream()
                .map(reviewMapper::toReviewDTO)
                //.map(review -> reviewMapper.toReviewDTO(review)) cách 2
                .collect(Collectors.toList());

        return reDTOs;
    }

    //get all reviews of a user
    @GetMapping("/user/{userId}")
    public List<ReviewDTO> getReviewsByUserId(@PathVariable int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) return null;

        List<ReviewDTO> reDTOs = user.getReviews().stream()
                .map(reviewMapper::toReviewDTOofUser)
                .collect(Collectors.toList());
        return reDTOs;
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO, Principal principal) {
        // Lấy user từ principal (token)
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if(user == null) return ResponseEntity.notFound().build();

        Book book = bookRepository.findById(reviewDTO.getBook().getId()).orElse(null);
        if(book == null) return ResponseEntity.notFound().build();

        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setContent(reviewDTO.getContent());
        review.setBook(book);
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(reviewMapper.toReviewDTO(reviewRepository.save(review)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable int id, @RequestBody ReviewDTO reviewDTO) {
        return reviewRepository.findById(id)
                .map(review -> {
                    review.setRating(reviewDTO.getRating());
                    review.setContent(reviewDTO.getContent());
                    Review savedReview = reviewRepository.save(review);
                    return ResponseEntity.ok().body(reviewMapper.toReviewDTO(savedReview));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable int id) {
        return reviewRepository.findById(id)
                .map(review -> {
                    reviewRepository.delete(review);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
