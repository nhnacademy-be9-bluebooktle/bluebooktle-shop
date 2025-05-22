package shop.bluebooktle.backend.coupon.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.service.UserCouponService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon")
public class UserCouponController {

	private final UserCouponService userCouponService;

	// TODO [쿠폰] 유저 별 쿠폰 전체 조회

	// TODO [쿠폰] 유저 별 사용 가능한 쿠폰 전체 조회
}
