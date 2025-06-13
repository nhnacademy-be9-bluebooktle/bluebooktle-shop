package shop.bluebooktle.backend.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.review.request.ReviewRegisterRequest;
import shop.bluebooktle.common.dto.review.request.ReviewUpdateRequest;
import shop.bluebooktle.common.dto.review.response.ReviewResponse;

public interface ReviewService {
	ReviewResponse addReview(Long userId, Long bookOrderId, ReviewRegisterRequest reviewRegisterRequest);

	ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest);

	Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable);

	Page<ReviewResponse> getReviewsForBook(Long bookId, Pageable pageable);

	boolean toggleReviewLike(Long userId, Long reviewId);
}
