package shop.bluebooktle.backend.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.entity.Review;

public interface ReviewQueryRepository {

	Page<Review> findReviewsByUserId(Long userId, Pageable pageable);

	Page<Review> findReviewsByBookId(Long bookId, Pageable pageable);
}
