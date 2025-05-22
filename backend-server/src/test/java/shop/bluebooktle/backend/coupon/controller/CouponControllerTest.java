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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import shop.bluebooktle.common.service.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(controllers = CouponController.class)
@AutoConfigureMockMvc(addFilters = false) //Security 필터 비활성화
class CouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CouponService couponService;

	@MockitoBean
	private CouponBatchLauncher couponBatchLauncher;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("쿠폰 등록 - 성공")
	void registerCoupon_success() throws Exception {
		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.name("할인 쿠폰")
			.couponTypeId(1L)
			.bookId(2L)
			.build();

		mockMvc.perform(post("/api/admin/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		verify(couponService).registerCoupon(any(CouponRegisterRequest.class));
	}

	@Test
	@DisplayName("쿠폰 전체 조회 - 성공")
	void getAllCoupons_success() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		CouponResponse response = new CouponResponse(
			1L,
			"여름 쿠폰",
			CouponTypeTarget.ORDER,
			"쿠폰 정책 이름",
			BigDecimal.valueOf(5000),
			LocalDateTime.now(),
			null,
			null
		);
		Page<CouponResponse> page = new PageImpl<>(List.of(response), pageable, 1);

		given(couponService.getAllCoupons(any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/api/admin/coupons")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content[0].couponName").value("여름 쿠폰"));

		verify(couponService).getAllCoupons(any(Pageable.class));
	}

	@Test
	@DisplayName("쿠폰 발급 - 성공")
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

		verify(couponBatchLauncher).run(any(UserCouponRegisterRequest.class));
	}
}
