package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.frontend.repository.CouponRepository;
import shop.bluebooktle.frontend.service.impl.CouponServiceImpl;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

	@Mock
	CouponRepository couponRepository;

	@InjectMocks
	CouponServiceImpl couponService;

	@Test
	@DisplayName("유저 쿠폰 전체 조회")
	void getAllCouponUsable() {
		Pageable pageable = PageRequest.of(0, 10);
		List<UserCouponResponse> content = List.of(new UserCouponResponse());
		Page<UserCouponResponse> page = new PageImpl<>(content, pageable, content.size());
		PaginationData<UserCouponResponse> response = new PaginationData<>(page);

		when(couponRepository.getAllCoupons(UserCouponFilterType.USABLE, pageable)).thenReturn(response);

		PaginationData<UserCouponResponse> result = couponService.getAllCoupons("usable", pageable);

		assertThat(result.getContent()).hasSize(1);
		verify(couponRepository).getAllCoupons(UserCouponFilterType.USABLE, pageable);
	}

	@Test
	@DisplayName("주문에 사용 가능한 쿠폰 조회")
	void getUsableCouponForOrder() {
		Long bookId1 = 1L;
		Long bookId2 = 2L;

		UsableUserCouponResponse bookCoupon = UsableUserCouponResponse.builder()
			.userCouponId(1L)
			.couponId(1L)
			.couponName("java 기초 도서 쿠폰")
			.minimumPayment(BigDecimal.valueOf(10000))
			.discountPrice(BigDecimal.valueOf(1000))
			.bookName("java 기초")
			.build();

		UsableUserCouponResponse orderCoupon = UsableUserCouponResponse.builder()
			.userCouponId(2L)
			.couponId(2L)
			.couponName("웰컴 주문 쿠폰")
			.minimumPayment(BigDecimal.valueOf(10000))
			.discountPrice(BigDecimal.valueOf(1000))
			.build();

		UsableUserCouponMapResponse mapResponse = new UsableUserCouponMapResponse();
		mapResponse.setUsableUserCouponMap(Map.of(
			-1L, List.of(orderCoupon),
			bookId1, List.of(bookCoupon)
		));
		when(couponRepository.getUsableCouponsForOrder(List.of(bookId1, bookId2))).thenReturn(mapResponse);

		UsableUserCouponMapResponse result = couponService.getUsableCouponsForOrder(List.of(bookId1, bookId2));

		assertThat(result.getUsableUserCouponMap()).containsKeys(-1L, bookId1);
		assertThat(result.getUsableUserCouponMap().get(-1L)).hasSize(1);
		assertThat(result.getUsableUserCouponMap().get(bookId1).get(0).getBookName()).isEqualTo("java 기초");

		verify(couponRepository).getUsableCouponsForOrder(List.of(bookId1, bookId2));
	}

	@Test
	@DisplayName("사용 가능한 쿠폰 개수 조회")
	void countAllUsableCoupons() {
		when(couponRepository.countAllUsableCoupons()).thenReturn(3L);

		Long result = couponService.countAllUsableCoupons();

		assertThat(result).isEqualTo(3L);
		verify(couponRepository).countAllUsableCoupons();
	}

	@Test
	@DisplayName("이번 달 만료 예정 쿠폰 개수 조회")
	void countExpiringThisMonth() {
		when(couponRepository.countExpiringThisMonth()).thenReturn(2L);

		Long result = couponService.countExpiringThisMonth();

		assertThat(result).isEqualTo(2L);
		verify(couponRepository).countExpiringThisMonth();
	}
}
