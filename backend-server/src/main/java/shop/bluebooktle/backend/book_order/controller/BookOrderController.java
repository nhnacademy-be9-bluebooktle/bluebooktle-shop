package shop.bluebooktle.backend.book_order.controller;

import org.springframework.data.domain.Pageable;
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
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingRequest;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
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
	public JsendResponse<BookOrderResponse> createBookOrder(@RequestBody @Valid BookOrderRequest request) {
		return JsendResponse.success(bookOrderService.createBookOrder(request));
	}

	/** 도서 주문 단건 조회 */
	@GetMapping("/{bookOrderId}")
	public JsendResponse<BookOrderResponse> getBookOrder(@PathVariable Long bookOrderId) {
		return JsendResponse.success(bookOrderService.getBookOrder(bookOrderId));
	}

	/** 도서 주문 수정 */
	@PutMapping("/{bookOrderId}")
	public JsendResponse<BookOrderResponse> updateBookOrder(@PathVariable Long bookOrderId,
		@RequestBody @Valid BookOrderRequest request) {
		return JsendResponse.success(bookOrderService.updateBookOrder(request));
	}

	/** 도서 주문 삭제 */
	@DeleteMapping("/{bookOrderId}")
	public JsendResponse<Void> deleteBookOrder(@PathVariable Long bookOrderId) {
		bookOrderService.deleteBookOrder(bookOrderId);
		return JsendResponse.success();
	}

	/** 포장 추가 */
	@PostMapping("/{bookOrderId}/packaging")
	public JsendResponse<OrderPackagingResponse> addPackaging(@PathVariable Long bookOrderId,
		@RequestBody @Valid OrderPackagingRequest request) {
		return JsendResponse.success(orderPackagingService.addOrderPackaging(request));
	}

	/** 포장 단건 조회 */
	@GetMapping("/packagings/{orderPackagingId}")
	public JsendResponse<OrderPackagingResponse> getPackaging(@PathVariable Long orderPackagingId) {
		return JsendResponse.success(orderPackagingService.getOrderPackaging(orderPackagingId));
	}

	/** 포장 수정 */
	@PutMapping("/packagings/{orderPackagingId}")
	public JsendResponse<OrderPackagingResponse> updatePackaging(@PathVariable Long orderPackagingId,
		@RequestBody @Valid OrderPackagingRequest request) {
		return JsendResponse.success(orderPackagingService.updateOrderPackaging(orderPackagingId, request));
	}

	/** 포장 삭제 */
	@DeleteMapping("/packagings/{orderPackagingId}")
	public JsendResponse<Void> deletePackaging(@PathVariable Long orderPackagingId) {
		orderPackagingService.deleteOrderPackaging(orderPackagingId);
		return JsendResponse.success();
	}

	/** 포장 옵션 등록 */
	@PostMapping("/options")
	public JsendResponse<PackagingOptionResponse> createPackagingOption(
		@RequestBody @Valid PackagingOptionRequest request) {
		return JsendResponse.success(packagingOptionService.createPackagingOption(request));
	}

	/** 포장 옵션 전체 조회 */
	@GetMapping("/options")
	public JsendResponse<PaginationData<PackagingOptionResponse>> getPackagingOptions(Pageable pageable) {
		return JsendResponse.success(new PaginationData<>(packagingOptionService.getPackagingOption(pageable)));
	}

	/** 포장 옵션 수정 */
	@PutMapping("/options")
	public JsendResponse<PackagingOptionResponse> updatePackagingOption(
		@RequestBody @Valid PackagingOptionRequest request) {
		return JsendResponse.success(packagingOptionService.updatePackagingOption(request));
	}

	/** 포장 옵션 삭제 */
	@DeleteMapping("/options/{packagingOptionId}")
	public JsendResponse<Void> deletePackagingOption(@PathVariable Long packagingOptionId) {
		packagingOptionService.deletePackagingOption(packagingOptionId);
		return JsendResponse.success();
	}
}
