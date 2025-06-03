package shop.bluebooktle.backend.coupon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.service.UserCouponService;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
@Tag(name = "쿠폰 조회 API", description = "MyPage 쿠폰 조회 API")
public class UserCouponController {

	private final UserCouponService userCouponService;

	@Operation(summary = "주문에 해당하는 조건의 쿠폰 조회", description = "유저가 보유한 쿠폰 중 주문에 해당하는 쿠폰을 조회합니다.")
	@GetMapping("/usable-order")
	public ResponseEntity<UsableUserCouponMapResponse> getUsableCoupons(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam List<Long> bookIds
	) {
		UsableUserCouponMapResponse response = userCouponService.getUsableCouponsForOrder(userPrincipal.getUserId(),
			bookIds);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "쿠폰 사용", description = "유저가 보유한 쿠폰 중 주문에 해당하는 쿠폰을 사용합니다.")
	@PostMapping("/{id}/use")
	public ResponseEntity<Void> useCoupon(@PathVariable Long id) {
		userCouponService.useCoupon(id);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "쿠폰 사용 취소", description = "사용했던 쿠폰을 취소합니다.")
	@PostMapping("/{id}/cancel")
	public ResponseEntity<Void> cancelCoupon(@PathVariable Long id) {
		userCouponService.cancelCouponUse(id);
		return ResponseEntity.ok().build();
	}

}
