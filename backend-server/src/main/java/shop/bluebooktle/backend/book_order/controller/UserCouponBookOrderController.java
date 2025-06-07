package shop.bluebooktle.backend.book_order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import shop.bluebooktle.backend.book_order.service.UserCouponBookOrderService;
import shop.bluebooktle.common.dto.book_order.request.UserCouponBookOrderRequest;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequestMapping("/api/user-coupons/usages")
@RequiredArgsConstructor
@Tag(name = "주문 쿠폰 API", description = "주문에서 사용한 도서 쿠폰 관련 API")
public class UserCouponBookOrderController {

	private final UserCouponBookOrderService userCouponBookOrderService;

	@PostMapping
	@Operation(summary = "주문에 사용한 도서 쿠폰", description = "주문에서 사용한 도서 쿠폰을 저장하고 사용 처리합니다.")
	public ResponseEntity<JsendResponse<Void>> createUserCouponBookOrder(
		@Valid @RequestBody UserCouponBookOrderRequest request,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		UserCouponBookOrderRequest updatedRequest = request.toBuilder()
			.userId(userPrincipal.getUserId())
			.build();

		userCouponBookOrderService.saveCouponUsage(updatedRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@DeleteMapping
	@Operation(summary = "주문에 사용한 도서 쿠폰 삭제", description = "주문에서 사용했던 도서 쿠폰을 사용 취소 처리합니다.")
	public ResponseEntity<JsendResponse<Void>> deleteUserCouponBookOrder(
		@RequestParam Long orderId,
		@RequestParam List<Long> userCouponIds
	) {
		userCouponBookOrderService.deleteCouponUsage(orderId, userCouponIds);
		return ResponseEntity.status(HttpStatus.OK).body(JsendResponse.success());
	}
}
