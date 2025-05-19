package shop.bluebooktle.backend.book_order.controller;

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
import shop.bluebooktle.common.dto.book_order.request.BookOrderRegisterRequest;
import shop.bluebooktle.common.dto.book_order.request.BookOrderUpdateRequest;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingRegisterRequest;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.BookOrderResponse;
import shop.bluebooktle.common.dto.book_order.response.OrderPackagingResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api/book-orders")
@RequiredArgsConstructor
public class BookOrderController {
	private final BookOrderService bookOrderService;
	private final OrderPackagingService orderPackagingService;

	/** 도서 주문 등록 */
	@PostMapping
	public ResponseEntity<JsendResponse<BookOrderResponse>> createBookOrder(
		@Valid @RequestBody BookOrderRegisterRequest request) {
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
		@RequestBody @Valid BookOrderUpdateRequest request,
		@PathVariable Long bookOrderId) {
		return ResponseEntity.ok(JsendResponse.success(bookOrderService.updateBookOrder(bookOrderId, request)));
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
		@PathVariable Long bookOrderId,
		@RequestBody @Valid OrderPackagingRegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(JsendResponse.success(orderPackagingService.addOrderPackaging(bookOrderId, request)));
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
}
