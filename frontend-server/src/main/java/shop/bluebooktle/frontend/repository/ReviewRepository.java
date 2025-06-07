package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book.request.ReviewRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", name = "reviewRepository", path = "/api/orders/reviews", configuration = FeignGlobalConfig.class)
public interface ReviewRepository {

	@PostMapping("/{bookOrderId}")
	ReviewResponse addReview(
		@PathVariable("bookOrderId") Long bookOrderId,
		@RequestBody ReviewRequest reviewRequest
	);

	@GetMapping
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
	Boolean toggleReviewLike(
		@PathVariable("reviewId") Long reviewId
	);

}
