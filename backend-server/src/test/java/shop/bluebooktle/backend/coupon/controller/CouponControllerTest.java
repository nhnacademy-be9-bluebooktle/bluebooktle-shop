package shop.bluebooktle.backend.coupon.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.coupon.batch.direct.DirectCouponBatchLauncher;
import shop.bluebooktle.backend.coupon.service.CouponService;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(controllers = CouponController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) //Security 필터 비활성화
class CouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CouponService couponService;

	@MockitoBean
	private DirectCouponBatchLauncher directCouponBatchLauncher;

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
		// given
		Pageable pageable = Pageable.ofSize(10);
		Page<CouponResponse> mockPage = Page.empty(pageable);
		given(couponService.getAllCoupons(eq("할인"), any(Pageable.class)))
			.willReturn(mockPage);

		// when & then
		mockMvc.perform(get("/api/admin/coupons")
				.param("searchCouponName", "할인")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(couponService).getAllCoupons(eq("할인"), any(Pageable.class));
	}

	@Test
	@DisplayName("쿠폰 직접 발급 - 성공")
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

		verify(directCouponBatchLauncher).run(any(UserCouponRegisterRequest.class));
	}
}
