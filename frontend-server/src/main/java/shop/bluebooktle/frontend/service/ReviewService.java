package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.book.request.ReviewRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;

public interface ReviewService {
	void addReview(Long userId, Long bookOrderId, ReviewRequest reviewRequest);

	void updateReivew(Long reivewId, ReviewRequest reviewRequest);

	Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable);

	Page<ReviewResponse> getReviewsForBook(Long bookId, Pageable pageable);

	boolean toggleReviewLike(Long reviewId);

}
