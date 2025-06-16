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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "도서 주문 API", description = "도서 주문에 관련 조회 및 관리를 합니다.")
public class BookOrderController {
	private final BookOrderService bookOrderService;
	private final OrderPackagingService orderPackagingService;

	@Operation(summary= "도서 주문 등록", description = "도서 주문을 등록합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<BookOrderResponse>> createBookOrder(
		@Valid @RequestBody BookOrderRegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(JsendResponse.success(bookOrderService.createBookOrder(request)));
	}

	@Operation(summary= "도서 주문 단건 조회", description = "도서 주문을 조회합니다.")
	@GetMapping("/{book-order-id}")
	public ResponseEntity<JsendResponse<BookOrderResponse>> getBookOrder(
		@PathVariable(name = "book-order-id") Long bookOrderId) {
		return ResponseEntity.ok(JsendResponse.success(bookOrderService.getBookOrder(bookOrderId)));
	}

	@Operation(summary= "도서 주문 수정", description = "도서 주문을 수정합니다.")
	@PutMapping("/{book-order-id}")
	public ResponseEntity<JsendResponse<BookOrderResponse>> updateBookOrder(
		@RequestBody @Valid BookOrderUpdateRequest request,
		@PathVariable(name = "book-order-id") Long bookOrderId) {
		return ResponseEntity.ok(JsendResponse.success(bookOrderService.updateBookOrder(bookOrderId, request)));
	}

	@Operation(summary= "도서 주문 삭제", description = "도서 주문을 삭제합니다.")
	@DeleteMapping("/{book-order-id}")
	public ResponseEntity<JsendResponse<Void>> deleteBookOrder(
		@PathVariable(name = "book-order-id") Long bookOrderId) {
		bookOrderService.deleteBookOrder(bookOrderId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary= "도서 주문 포장 추가", description = "도서 주문에 대한 포장을 추가합니다.")
	@PostMapping("/{book-order-id}/packaging")
	public ResponseEntity<JsendResponse<OrderPackagingResponse>> addPackaging(
		@PathVariable(name = "book-order-id") Long bookOrderId,
		@RequestBody @Valid OrderPackagingRegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(JsendResponse.success(orderPackagingService.addOrderPackaging(bookOrderId, request)));
	}

	@Operation(summary= "포장 단건 조회", description = "포장에 대한 정보를 조회합니다.")
	@GetMapping("/packagings/{order-packaging-id}")
	public ResponseEntity<JsendResponse<OrderPackagingResponse>> getPackaging(
		@PathVariable(name = "order-packaging-id") Long orderPackagingId) {
		return ResponseEntity.ok(JsendResponse.success(orderPackagingService.getOrderPackaging(orderPackagingId)));
	}

	@Operation(summary= "포장 수정", description = "포장 정보를 수정합니다.")
	@PutMapping("/packagings/{order-packaging-id}")
	public ResponseEntity<JsendResponse<OrderPackagingResponse>> updatePackaging(
		@PathVariable(name = "order-packaging-id") Long orderPackagingId,
		@RequestBody @Valid OrderPackagingUpdateRequest request) {
		return ResponseEntity.ok(
			JsendResponse.success(orderPackagingService.updateOrderPackaging(orderPackagingId, request)));
	}

	@Operation(summary= "포장 삭제", description = "포장 정보를 삭제합니다.")
	@DeleteMapping("/packagings/{order-packaging-id}")
	public ResponseEntity<JsendResponse<Void>> deletePackaging(
		@PathVariable(name = "order-packaging-id") Long orderPackagingId) {
		orderPackagingService.deleteOrderPackaging(orderPackagingId);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
