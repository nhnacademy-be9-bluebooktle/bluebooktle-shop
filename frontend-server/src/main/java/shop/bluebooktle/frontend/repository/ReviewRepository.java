package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.review.request.ReviewRegisterRequest;
import shop.bluebooktle.common.dto.review.request.ReviewUpdateRequest;
import shop.bluebooktle.common.dto.review.response.ReviewResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", name = "reviewRepository", path = "/api/orders/reviews", configuration = FeignGlobalConfig.class)
public interface ReviewRepository {

	@PostMapping("/{bookOrderId}")
	@RetryWithTokenRefresh
	ReviewResponse addReview(
		@PathVariable("bookOrderId") Long bookOrderId,
		@RequestBody ReviewRegisterRequest reviewRegisterRequest
	);

	@PutMapping("/{reviewId}")
	@RetryWithTokenRefresh
	ReviewResponse updateReview(
		@PathVariable("reviewId") Long reviewId,
		@RequestBody ReviewUpdateRequest reviewUpdateRequest
	);

	@GetMapping("/me")
	@RetryWithTokenRefresh
	PaginationData<ReviewResponse> getMyReviews(
		@RequestParam("page") int page,
		@RequestParam("size") int size
	);

	@GetMapping("/book/{bookId}")
	PaginationData<ReviewResponse> getReviewsForBook(
		@PathVariable("bookId") Long bookId,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	);

	@PostMapping("/{reviewId}/like")
	@RetryWithTokenRefresh
	Boolean toggleReviewLike(
		@PathVariable("reviewId") Long reviewId
	);

}
