package shop.bluebooktle.backend.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.ReviewService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.book.request.ReviewRegisterRequest;
import shop.bluebooktle.common.dto.book.request.ReviewUpdateRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.security.Auth;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	// 리뷰 작성
	@PostMapping("/{book-order-id}")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<ReviewResponse>> addReview(
		@PathVariable("book-order-id") Long bookOrderId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody @Valid ReviewRegisterRequest reviewRegisterRequest
	) {
		Long userId = userPrincipal.getUserId();
		ReviewResponse created = reviewService.addReview(userId, bookOrderId, reviewRegisterRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(created));
	}

	// 리뷰 수정
	@PutMapping("/{review-id}")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<ReviewResponse>> updateReview(
		@PathVariable("review-id") Long reviewId,
		@RequestBody @Valid ReviewUpdateRequest reviewUpdateRequest
	) {
		ReviewResponse updatedReview = reviewService.updateReview(reviewId, reviewUpdateRequest);
		return ResponseEntity.ok(JsendResponse.success(updatedReview));
	}

	// 내가 쓴 리뷰 목록 조회
	@GetMapping("/me")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<PaginationData<ReviewResponse>>> getMyReviews(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		Long userId = userPrincipal.getUserId();

		Page<ReviewResponse> pageResult = reviewService.getMyReviews(userId, PageRequest.of(page, size));
		PaginationData<ReviewResponse> paginationData = new PaginationData<>(pageResult);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	// 도서 상세 페이지에서 리뷰 목록 조회
	@GetMapping("/book/{book-id}")
	public ResponseEntity<JsendResponse<PaginationData<ReviewResponse>>> getReviewsForBook(
		@PathVariable("book-id") Long bookId,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size
	) {
		Page<ReviewResponse> pageResult = reviewService.getReviewsForBook(bookId, PageRequest.of(page, size));
		PaginationData<ReviewResponse> paginationData = new PaginationData<>(pageResult);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	// 리뷰 좋아요
	@PostMapping("/{review-id}/like")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<Boolean>> toggleReviewLike(
		@PathVariable("review-id") Long reviewId,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		Long userId = userPrincipal.getUserId();
		boolean liked = reviewService.toggleReviewLike(reviewId, userId);
		return ResponseEntity.ok(JsendResponse.success(liked));
	}
}
