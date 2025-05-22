package shop.bluebooktle.backend.coupon.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.coupon.batch.CouponBatchLauncher;
import shop.bluebooktle.backend.coupon.service.CouponService;
import shop.bluebooktle.common.domain.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;

@WebMvcTest(controllers = CouponController.class)
class CouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CouponService couponService;

	@MockitoBean
	private CouponBatchLauncher couponBatchLauncher;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("쿠폰 등록 - 성공")
	void registerCoupon_success() throws Exception {
		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.name("10% 할인 쿠폰")
			.couponTypeId(1L)
			.bookId(2L)
			.build();

		mockMvc.perform(post("/api/admin/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		verify(couponService).registerCoupon(any());
	}

	@Test
	@DisplayName("쿠폰 전체 조회 - 성공")
	void getAllCoupons_success() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		CouponResponse sample = new CouponResponse(
			1L,
			"무더위 쿠폰",
			CouponTypeTarget.ORDER,
			"3만원 이상 구매 시 2천원 할인",
			BigDecimal.valueOf(100),
			LocalDateTime.now(),
			null,
			null
		);
		Page<CouponResponse> couponPage = new PageImpl<>(List.of(sample), pageable, 1);

		given(couponService.getAllCoupons(any())).willReturn(couponPage);

		mockMvc.perform(get("/api/admin/coupons")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content[0].couponName").value("무더위 쿠폰"));

		verify(couponService).getAllCoupons(any());
	}

	@Test
	@DisplayName("쿠폰 발급 요청 - 성공")
	void registerUserCoupon_success() throws Exception {
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(1L)
			.availableStartAt(LocalDateTime.now())
			.availableEndAt(LocalDateTime.now().plusDays(7))
			.build();

		mockMvc.perform(post("/api/admin/coupons/issue")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		verify(couponBatchLauncher).run(any());
	}
}

