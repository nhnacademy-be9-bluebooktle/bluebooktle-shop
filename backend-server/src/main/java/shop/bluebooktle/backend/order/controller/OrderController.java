package shop.bluebooktle.backend.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;
import shop.bluebooktle.common.security.Auth;
import shop.bluebooktle.common.security.UserPrincipal;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "주문 API", description = "주문 조회 및 결제 관련 API")
public class OrderController {

	private final OrderService orderService;

	private void checkPrincipal(UserPrincipal userPrincipal) {
		if (userPrincipal == null || userPrincipal.getUserId() == null) {
			throw new InvalidTokenException();
		}
	}

	@Operation(summary = "주문 확인 및 결제 정보 조회(orderId)", description = "특정 주문 ID에 대한 상세 내역(주문 도서, 배송, 쿠폰, 포인트)을 조회하여 결제 전 확인 페이지에 사용합니다.")
	@GetMapping("/{orderId}")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<OrderConfirmDetailResponse>> getOrderConfirmationDetails(
		@Parameter(description = "조회할 주문의 ID") @PathVariable Long orderId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
		checkPrincipal(userPrincipal);
		OrderConfirmDetailResponse responseDto = orderService.getOrderById(orderId,
			userPrincipal.getUserId());
		return ResponseEntity.ok(JsendResponse.success(responseDto));
	}

	@Operation(summary = "주문 확인 및 결제 정보 조회", description = "특정 주문 KEY에 대한 상세 내역(주문 도서, 배송, 쿠폰, 포인트)을 조회하여 결제 전 확인 페이지에 사용합니다.")
	@GetMapping("/key/{orderKey}")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<OrderConfirmDetailResponse>> getOrderConfirmationDetails(
		@Parameter(description = "조회할 주문의 KEY") @PathVariable String orderKey,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
		checkPrincipal(userPrincipal);
		OrderConfirmDetailResponse responseDto = orderService.getOrderByKey(orderKey,
			userPrincipal.getUserId());
		return ResponseEntity.ok(JsendResponse.success(responseDto));
	}

	@Operation(summary = "주문 생성", description = "새 주문을 생성합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Long>> createOrder(
		@Valid @RequestBody OrderCreateRequest request
	) {
		Long createdOrderId = orderService.createOrder(request);
		return ResponseEntity.ok(JsendResponse.success(createdOrderId));
	}

	@Operation(summary = "내 주문 전체 조회", description = "로그인한 사용자의 모든 결제 완료 주문 내역을 페이징 조회합니다.")
	@GetMapping("/history")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<PaginationData<OrderHistoryResponse>>> getOrderHistory(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam(value = "status", required = false) OrderStatus status,
		@PageableDefault(sort = "createdAt") Pageable pageable
	) {
		checkPrincipal(userPrincipal);
		Page<OrderHistoryResponse> page = orderService.getUserOrders(
			userPrincipal.getUserId(),
			status,
			pageable
		);
		PaginationData<OrderHistoryResponse> paginationData = new PaginationData<>(page);
		return ResponseEntity
			.ok(JsendResponse.success(paginationData));
	}
}