package shop.bluebooktle.backend.coupon.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import shop.bluebooktle.backend.config.WithMockCustomUser;
import shop.bluebooktle.backend.coupon.service.UserCouponService;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(controllers = UserCouponController.class)
@ActiveProfiles("test")
class UserCouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserCouponService userCouponService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	private List<UserCouponResponse> dummyCoupons;
	private UsableUserCouponMapResponse dummyUsableCouponMapResponse;

	@BeforeEach
	void setUp() {
		LocalDateTime now = LocalDateTime.of(2025, 6, 1, 0, 0);

		UserCouponResponse orderCoupon = new UserCouponResponse(
			1L, now, "주문 쿠폰", "정책1", CouponTypeTarget.ORDER,
			now, now.plusDays(10), null, null, null
		);
		UserCouponResponse bookCoupon = new UserCouponResponse(
			1L, now, "도서 쿠폰", "정책2", CouponTypeTarget.BOOK,
			now, now.plusDays(10), null, null, "자바 기초"
		);
		UserCouponResponse categoryCoupon = new UserCouponResponse(
			1L, now, "카테고리 쿠폰", "정책1", CouponTypeTarget.BOOK,
			now, now.plusDays(100), null, "IT", null
		);
		dummyCoupons = List.of(orderCoupon, bookCoupon, categoryCoupon);

		UsableUserCouponResponse usableCoupon = UsableUserCouponResponse.builder()
			.userCouponId(1L)
			.couponId(101L)
			.couponName("할인 쿠폰")
			.couponTypeName("정책 A")
			.minimumPayment(BigDecimal.valueOf(5000))
			.discountPrice(BigDecimal.valueOf(1000))
			.maximumDiscountPrice(null)
			.discountPercent(null)
			.bookName(null)
			.categoryName(null)
			.build();

		dummyUsableCouponMapResponse = new UsableUserCouponMapResponse();
		dummyUsableCouponMapResponse.setUsableUserCouponMap(
			Map.of(
				-1L, List.of(usableCoupon),
				1L, List.of(),
				2L, List.of()
			)
		);
	}

	@Test
	@WithMockCustomUser
	@DisplayName("보유한 유저 쿠폰 조회 - 성공")
	void getAllUserCoupons_success() throws Exception {
		// Pageable pageable = PageRequest.of(0, 10);
		given(userCouponService.getAllUserCoupons(eq(1L), eq(UserCouponFilterType.ALL), any(Pageable.class)))
			.willReturn(new PageImpl<>(dummyCoupons));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/coupons")
				.param("target", "ALL")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("주문에 사용 가능한 쿠폰 조회")
	void getUsableCoupons() throws Exception {
		given(userCouponService.getUsableCouponsForOrder(eq(1L), eq(List.of(1L, 2L))))
			.willReturn(dummyUsableCouponMapResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/coupons/usable-order")
				.param("bookIds", "1", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.usableUserCouponMap['-1']").isArray()
			);
	}

	@Test
	@WithMockCustomUser
	@DisplayName("사용 가능 쿠폰 개수 조회")
	void countAllUsableCoupons() throws Exception {
		given(userCouponService.countAllUsableCoupons(1L)).willReturn(3L);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/coupons/count/usable"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value(3L));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("이번 달 내 소멸 예정 쿠폰 개수 조회")
	void countExpiringThisMonth() throws Exception {
		given(userCouponService.countExpiringThisMonth(1L)).willReturn(1L);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/coupons/count/expiring"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value(1L));
	}
}