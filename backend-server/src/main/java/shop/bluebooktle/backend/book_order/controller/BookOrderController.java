package shop.bluebooktle.backend.book_order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book_order.service.BookOrderService;
import shop.bluebooktle.backend.book_order.service.OrderPackagingService;
import shop.bluebooktle.backend.book_order.service.PackagingOptionService;
import shop.bluebooktle.common.dto.book_order.request.BookOrderRequest;
import shop.bluebooktle.common.dto.book_order.request.BookOrderUpdateRequest;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingRequest;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingUpdateRequest;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.BookOrderResponse;
import shop.bluebooktle.common.dto.book_order.response.OrderPackagingResponse;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/book-orders")
@RequiredArgsConstructor
public class BookOrderController {
	private final BookOrderService bookOrderService;
	private final OrderPackagingService orderPackagingService;
	private final PackagingOptionService packagingOptionService;

	/** 도서 주문 등록 */
	@PostMapping
	public ResponseEntity<JsendResponse<BookOrderResponse>> createBookOrder(
		@Valid @RequestBody BookOrderRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(JsendResponse.success(bookOrderService.createBookOrder(request)));
	}

	/** 도서 주문 단건 조회 */
	@GetMapping("/{bookOrderId}")
	public ResponseEntity<JsendResponse<BookOrderResponse>> getBookOrder(
		@PathVariable Long bookOrderId) {
		return ResponseEntity.ok(JsendResponse.success(bookOrderService.getBookOrder(bookOrderId)));
	}

	/** 도서 주문 수정 */
	@PutMapping("/{bookOrderId}")
	public ResponseEntity<JsendResponse<BookOrderResponse>> updateBookOrder(
		@RequestBody @Valid BookOrderUpdateRequest request) {
		return ResponseEntity.ok(JsendResponse.success(bookOrderService.updateBookOrder(request)));
	}

	/** 도서 주문 삭제 */
	@DeleteMapping("/{bookOrderId}")
	public ResponseEntity<JsendResponse<Void>> deleteBookOrder(
		@PathVariable Long bookOrderId) {
		bookOrderService.deleteBookOrder(bookOrderId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	/** 포장 추가 */
	@PostMapping("/{bookOrderId}/packaging")
	public ResponseEntity<JsendResponse<OrderPackagingResponse>> addPackaging(
		@RequestBody @Valid OrderPackagingRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(JsendResponse.success(orderPackagingService.addOrderPackaging(request)));
	}

	/** 포장 단건 조회 */
	@GetMapping("/packagings/{orderPackagingId}")
	public ResponseEntity<JsendResponse<OrderPackagingResponse>> getPackaging(
		@PathVariable Long orderPackagingId) {
		return ResponseEntity.ok(JsendResponse.success(orderPackagingService.getOrderPackaging(orderPackagingId)));
	}

	/** 포장 수정 */
	@PutMapping("/packagings/{orderPackagingId}")
	public ResponseEntity<JsendResponse<OrderPackagingResponse>> updatePackaging(
		@PathVariable Long orderPackagingId,
		@RequestBody @Valid OrderPackagingUpdateRequest request) {
		return ResponseEntity.ok(
			JsendResponse.success(orderPackagingService.updateOrderPackaging(orderPackagingId, request)));
	}

	/** 포장 삭제 */
	@DeleteMapping("/packagings/{orderPackagingId}")
	public ResponseEntity<JsendResponse<Void>> deletePackaging(
		@PathVariable Long orderPackagingId) {
		orderPackagingService.deleteOrderPackaging(orderPackagingId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	/** 포장 옵션 등록 */
	@PostMapping("/options")
	public ResponseEntity<JsendResponse<PackagingOptionResponse>> createPackagingOption(
		@RequestBody @Valid PackagingOptionRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(JsendResponse.success(packagingOptionService.createPackagingOption(request)));
	}

	/** 포장 옵션 전체 조회 */
	@GetMapping("/options")
	public ResponseEntity<JsendResponse<PaginationData<PackagingOptionResponse>>> getPackagingOptions(
		Pageable pageable) {
		Page<PackagingOptionResponse> resultPage = packagingOptionService.getPackagingOption(
			pageable); // 페이징 처리된 응답 객체 가져오기
		PaginationData<PackagingOptionResponse> paginationData = new PaginationData<>(resultPage); // PaginationData 감싸기
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	/** 포장 옵션 수정 */
	@PutMapping("/options")
	public ResponseEntity<JsendResponse<PackagingOptionResponse>> updatePackagingOption(
		@RequestBody @Valid PackagingOptionUpdateRequest request) {
		return ResponseEntity.ok(JsendResponse.success(packagingOptionService.updatePackagingOption(request)));
	}

	/** 포장 옵션 삭제 */
	@DeleteMapping("/options/{packagingOptionId}")
	public ResponseEntity<JsendResponse<Void>> deletePackagingOption(
		@PathVariable Long packagingOptionId) {
		packagingOptionService.deletePackagingOption(packagingOptionId);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
