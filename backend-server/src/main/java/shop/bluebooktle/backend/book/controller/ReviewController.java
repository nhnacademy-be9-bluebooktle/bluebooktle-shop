package shop.bluebooktle.backend.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.ReviewService;
import shop.bluebooktle.common.dto.book.request.ReviewRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books/{bookOrderId}/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	/**
	 * 리뷰 작성
	 * @param bookOrderId   PathVariable로 받은 주문 ID
	 * @param userPrincipal @AuthenticationPrincipal로 주입된 UserPrincipal (userId 포함)
	 * @param reviewRequest RequestBody로 받은 ReviewRequest DTO
	 */
	@PostMapping
	public ResponseEntity<JsendResponse<ReviewResponse>> addReview(
		@PathVariable Long bookOrderId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody @Valid ReviewRequest reviewRequest
	) {
		Long userId = userPrincipal.getUserId();
		ReviewResponse created = reviewService.addReview(userId, bookOrderId, reviewRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(created));
	}

	/**
	 * 특정 주문(bookOrderId)에 달린 리뷰를 페이징해서 조회
	 * @param bookOrderId   PathVariable로 받은 주문 ID
	 * @param page          쿼리 파라미터로 받은 페이지 번호 (기본값 0)
	 * @param size          쿼리 파라미터로 받은 페이지 크기 (기본값 10)
	 */
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<ReviewResponse>>> getReviews(
		@PathVariable Long bookOrderId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<ReviewResponse> reviews = reviewService.getReviews(bookOrderId, page, size);
		PaginationData<ReviewResponse> paginationData = new PaginationData<>(reviews);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}
}
