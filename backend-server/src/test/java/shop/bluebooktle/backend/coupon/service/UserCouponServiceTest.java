package shop.bluebooktle.backend.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.coupon.service.impl.UserCouponServiceImpl;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {

	@InjectMocks
	private UserCouponServiceImpl userCouponService;

	@Mock
	private UserCouponRepository userCouponRepository;

	@Test
	@DisplayName("보유한 쿠폰 조회")
	void getAllUserCoupons() {
		Long userId = 1L;
		Pageable pageable = PageRequest.of(0, 10);
		UserCouponFilterType filterType = UserCouponFilterType.ALL;

		Page<UserCouponResponse> dummyCoupons = new PageImpl<>(List.of(
			new UserCouponResponse(
				1L,
				LocalDateTime.now(),
				"쿠폰1",
				"정책1",
				CouponTypeTarget.ORDER,
				LocalDateTime.now(),
				LocalDateTime.now().plusDays(1),
				null,
				null,
				null
			)
		));

		given(userCouponRepository.findAllByUserCoupon(userId, filterType, pageable)).willReturn(dummyCoupons);

		Page<UserCouponResponse> result = userCouponService.getAllUserCoupons(userId, filterType, pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		verify(userCouponRepository).findAllByUserCoupon(userId, filterType, pageable);
	}

	@Test
	@DisplayName("주문에 해당하는 쿠폰 조회")
	void getUsableCouponsForOrder() {
		Long userId = 1L;
		List<Long> bookIds = List.of(1L, 2L);
		UsableUserCouponMapResponse response = new UsableUserCouponMapResponse();

		given(userCouponRepository.findAllByUsableUserCouponForOrder(userId, bookIds)).willReturn(response);

		UsableUserCouponMapResponse result = userCouponService.getUsableCouponsForOrder(userId, bookIds);

		assertThat(result).isNotNull();
		verify(userCouponRepository).findAllByUsableUserCouponForOrder(userId, bookIds);
	}

	@Test
	@DisplayName("주문에 해당하는 쿠폰 수 조회")
	void countAllUsableCoupons() {
		given(userCouponRepository.couponAllUsableCoupons(1L)).willReturn(3L);

		Long count = userCouponService.countAllUsableCoupons(1L);

		assertThat(count).isEqualTo(3L);
		verify(userCouponRepository).couponAllUsableCoupons(1L);
	}

	@Test
	@DisplayName("이번 달 소멸 예정 쿠폰 수 조회")
	void countExpiringThisMonth() {
		given(userCouponRepository.couponExpiringThisMonth(1L)).willReturn(1L);

		Long count = userCouponService.countExpiringThisMonth(1L);

		assertThat(count).isEqualTo(1L);
		verify(userCouponRepository).couponExpiringThisMonth(1L);
	}

}
