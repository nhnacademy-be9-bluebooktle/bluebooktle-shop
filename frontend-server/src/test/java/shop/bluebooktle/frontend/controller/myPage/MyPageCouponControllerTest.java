package shop.bluebooktle.frontend.controller.myPage;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;
import shop.bluebooktle.frontend.service.CouponService;

@WebMvcTest(controllers = MyPageCouponController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalUserInfoAdvice.class))
@ActiveProfiles("test")
class MyPageCouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CouponService couponService;

	@MockitoBean
	private CartService cartService;
	@MockitoBean
	private CategoryService categoryService;

	@Test
	@DisplayName("GET /mypage/coupons - 기본 조회")
	void userCouponsPage_default() throws Exception {
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"));

		UserCouponResponse response = new UserCouponResponse(
			1L,
			LocalDateTime.now().minusDays(1),
			"테스트 쿠폰",
			"정책A",
			CouponTypeTarget.ORDER,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			null,
			"소설",
			"쿠폰 적용 도서"
		);
		PaginationData<UserCouponResponse> pageData = new PaginationData<>(
			new PageImpl<>(List.of(response), pageable, 1L)
		);

		given(couponService.getAllCoupons(eq("ALL"), any(Pageable.class))).willReturn(pageData);
		given(couponService.countAllUsableCoupons()).willReturn(3L);
		given(couponService.countExpiringThisMonth()).willReturn(1L);

		mockMvc.perform(get("/mypage/coupons"))
			.andExpect(status().isOk())
			.andExpect(view().name("mypage/coupon_list"))
			.andExpect(model().attributeExists("currentURI"))
			.andExpect(model().attributeExists("page"))
			.andExpect(model().attributeExists("coupons"))
			.andExpect(model().attribute("selectedFilter", "ALL"))
			.andExpect(model().attribute("usableCouponCount", 3L))
			.andExpect(model().attribute("expiringCouponCount", 1L));
	}

	@Test
	@DisplayName("GET /mypage/coupons - 필터 적용")
	void userCouponsPage_withFilter() throws Exception {
		PaginationData<UserCouponResponse> pageData = new PaginationData<>(
			new PageImpl<>(List.of())
		);
		given(couponService.getAllCoupons(eq("USABLE"), any(Pageable.class))).willReturn(pageData);
		given(couponService.countAllUsableCoupons()).willReturn(0L);
		given(couponService.countExpiringThisMonth()).willReturn(0L);

		mockMvc.perform(get("/mypage/coupons").param("filter", "USABLE"))
			.andExpect(status().isOk())
			.andExpect(view().name("mypage/coupon_list"))
			.andExpect(model().attribute("selectedFilter", "USABLE"))
			.andExpect(model().attribute("usableCouponCount", 0L))
			.andExpect(model().attribute("expiringCouponCount", 0L));
	}
}

