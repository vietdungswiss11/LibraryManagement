package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Review;
import Ecommerce.BookWeb.Project.Repository.BookRepository;
import Ecommerce.BookWeb.Project.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private ReviewRepository reviewRepository;
    private BookRepository bookRepository;
    @Autowired
    ReviewController(ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return reviewRepository.findById(Math.toIntExact(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/book/{bookId}")
    public List<Review> getReviewsByBookId(@PathVariable Long bookId) {
        Optional<Book> book = bookRepository.findById(Math.toIntExact(bookId));
        if(book.isEmpty()) {
            return null;
        }
        return reviewRepository.findByBook(book.get());
    }

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewRepository.save(review);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review reviewDetails) {
        return reviewRepository.findById(Math.toIntExact(id))
                .map(review -> {
                    review.setRating(reviewDetails.getRating());
                    review.setContent(reviewDetails.getContent());
                    return ResponseEntity.ok(reviewRepository.save(review));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        return reviewRepository.findById(Math.toIntExact(id))
                .map(review -> {
                    reviewRepository.delete(review);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
