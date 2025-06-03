package shop.bluebooktle.backend.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/api/orders/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	//리뷰작성
	@PostMapping("{bookOrderId}")
	public ResponseEntity<JsendResponse<ReviewResponse>> addReview(
		@PathVariable Long bookOrderId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody @Valid ReviewRequest reviewRequest
	) {
		Long userId = userPrincipal.getUserId();
		ReviewResponse created = reviewService.addReview(userId, bookOrderId, reviewRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(created));
	}

	//내가쓴 리뷰 목록조회
	@GetMapping
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

}
