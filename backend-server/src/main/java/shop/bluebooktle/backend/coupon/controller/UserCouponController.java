package shop.bluebooktle.backend.coupon.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.service.UserCouponService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.security.Auth;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
@Tag(name = "쿠폰 조회 API", description = "MyPage 쿠폰 조회 API")
public class UserCouponController {

	private final UserCouponService userCouponService;

	@Operation(summary = "쿠폰 조회", description = "유저가 보유한 쿠폰을 조회합니다.")
	@GetMapping
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<PaginationData<UserCouponResponse>>> getAllUserCoupons(
		@RequestParam(name = "target", defaultValue = "ALL") UserCouponFilterType filter,
		@PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {

		Page<UserCouponResponse> couponPage = userCouponService.getAllUserCoupons(userPrincipal.getUserId(), filter,
			pageable);
		PaginationData<UserCouponResponse> paginationData = new PaginationData<>(couponPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(summary = "주문에 해당하는 조건의 쿠폰 조회", description = "유저가 보유한 쿠폰 중 주문에 해당하는 쿠폰을 조회합니다.")
	@GetMapping("/usable-order")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<UsableUserCouponMapResponse>> getUsableCoupons(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam(required = false) List<Long> bookIds
	) {
		UsableUserCouponMapResponse response = userCouponService.getUsableCouponsForOrder(userPrincipal.getUserId(),
			bookIds);
		return ResponseEntity.ok(JsendResponse.success(response));
	}

	@Operation(summary = "사용 가능 쿠폰 개수 조회", description = "현재 시점 기준으로 사용 가능한 모든 쿠폰의 개수를 반환합니다.")
	@GetMapping("/count/usable")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<Long>> countAllUsableCoupons(
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		Long count = userCouponService.countAllUsableCoupons(userPrincipal.getUserId());
		return ResponseEntity.ok(JsendResponse.success(count));
	}

	@Operation(summary = "이번 달 내 소멸 예정 쿠폰 개수 조회", description = "이번 달 안에 소멸 예정인 쿠폰의 개수를 반환합니다.")
	@GetMapping("/count/expiring")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<Long>> countExpiringThisMonth(
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		Long count = userCouponService.countExpiringThisMonth(userPrincipal.getUserId());
		return ResponseEntity.ok(JsendResponse.success(count));
	}

}
