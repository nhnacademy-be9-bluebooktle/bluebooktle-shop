package shop.bluebooktle.backend.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
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

	@Operation(summary = "주문 확인 및 결제 정보 조회", description = "특정 주문 ID에 대한 상세 내역(주문 도서, 배송, 쿠폰, 포인트)을 조회하여 결제 전 확인 페이지에 사용합니다.")
	@GetMapping("/{orderId}/confirmation")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<OrderConfirmDetailResponse>> getOrderConfirmationDetails(
		@Parameter(description = "조회할 주문의 ID") @PathVariable Long orderId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
		checkPrincipal(userPrincipal);
		log.info("Request to get order confirmation details for orderId: {} by userId: {}", orderId,
			userPrincipal.getUserId());
		OrderConfirmDetailResponse responseDto = orderService.getOrderDetailsForConfirmation(orderId,
			userPrincipal.getUserId());
		return ResponseEntity.ok(JsendResponse.success(responseDto));
	}

}