package shop.bluebooktle.backend.book.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.ReviewRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;

public interface ReviewService {
	ReviewResponse addReview(Long userId, Long bookOrderId, ReviewRequest reviewRequest);

	Page<ReviewResponse> getReviews(Long bookOrderId, int page, int size);
}
