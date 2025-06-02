package shop.bluebooktle.backend.coupon.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.coupon.service.CouponTypeService;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(controllers = CouponTypeController.class)
@AutoConfigureMockMvc(addFilters = false) //Security 필터 비활성화
class CouponTypeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CouponTypeService couponTypeService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("쿠폰 정책 등록 - 성공")
	@WithMockUser
	void registerCouponType_success() throws Exception {
		CouponTypeRegisterRequest request = CouponTypeRegisterRequest.builder()
			.name("도서 10% 할인")
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(new BigDecimal("10000"))
			.discountPercent(10)
			.build();

		mockMvc.perform(post("/api/admin/coupons/type")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)).with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		verify(couponTypeService).registerCouponType(any());
	}

	@Test
	@DisplayName("쿠폰 정책 전체 조회 - 성공")
	@WithMockUser
	void getAllCouponTypeList_success() throws Exception {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
		List<CouponTypeResponse> content = List.of(
			new CouponTypeResponse(1L, "책 할인", CouponTypeTarget.BOOK, new BigDecimal("10000"), new BigDecimal("1000"),
				null, null)
		);
		Page<CouponTypeResponse> page = new PageImpl<>(content, pageable, content.size());

		given(couponTypeService.getAllCouponTypeList(any())).willReturn(page);

		mockMvc.perform(get("/api/admin/coupons/type")
				.param("page", "0")
				.param("size", "10")
				.param("sort", "id"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content[0].id").value(1L))
			.andExpect(jsonPath("$.data.content[0].name").value("책 할인"));

		verify(couponTypeService).getAllCouponTypeList(any());
	}
}
