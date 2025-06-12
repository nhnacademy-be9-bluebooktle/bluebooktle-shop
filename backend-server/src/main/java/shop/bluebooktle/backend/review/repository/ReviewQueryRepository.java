package shop.bluebooktle.backend.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.review.entity.Review;

public interface ReviewQueryRepository {

	Page<Review> findReviewsByUserId(Long userId, Pageable pageable);

	Page<Review> findReviewsByBookId(Long bookId, Pageable pageable);
}
