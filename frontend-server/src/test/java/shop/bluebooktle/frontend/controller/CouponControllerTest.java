package shop.bluebooktle.frontend.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponResponse;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;
import shop.bluebooktle.frontend.service.CouponService;


@WebMvcTest(
	controllers = CouponController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = GlobalUserInfoAdvice.class
	)
)
@ActiveProfiles("test")
class CouponControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	CouponService couponService;

	@MockitoBean
	private CartService cartService;
	@MockitoBean
	private CategoryService categoryService;

	@Test
	@DisplayName("GET /coupons/usable-order - bookIds 파라미터 포함")
	void getUsableCouponsForOrder() throws Exception {
		Long bookId1 = 1L;
		Long bookId2 = 2L;

		UsableUserCouponResponse coupon1 = UsableUserCouponResponse.builder()
			.userCouponId(101L)
			.couponId(201L)
			.couponName("도서쿠폰")
			.couponTypeName("BOOK")
			.minimumPayment(BigDecimal.valueOf(10000))
			.discountPrice(BigDecimal.valueOf(2000))
			.build();

		UsableUserCouponMapResponse response = new UsableUserCouponMapResponse();
		response.setUsableUserCouponMap(Map.of(
			bookId1, List.of(coupon1),
			bookId2, List.of()
		));

		given(couponService.getUsableCouponsForOrder(List.of(bookId1, bookId2))).willReturn(response);

		mockMvc.perform(get("/coupons/usable-order")
				.param("bookIds", "1", "2")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.usableUserCouponMap['1'][0].couponName").value("도서쿠폰"));
	}
}

