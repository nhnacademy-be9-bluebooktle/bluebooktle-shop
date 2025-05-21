// package shop.bluebooktle.backend.coupon.controller;
//
// import static org.mockito.BDDMockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.List;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import shop.bluebooktle.backend.coupon.service.CouponService;
// import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
// import shop.bluebooktle.common.dto.coupon.request.CouponUpdateRequest;
// import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
//
// @WebMvcTest(controllers = CouponController.class)
// class CouponControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@MockitoBean
// 	private CouponService couponService;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@Test
// 	@DisplayName("쿠폰 등록 - 성공")
// 	@WithMockUser
// 	void registerCoupon_success() throws Exception {
// 		CouponRegisterRequest request = CouponRegisterRequest.builder()
// 			.couponTypeId(1L)
// 			.name("test coupon")
// 			.bookId(1L)
// 			.build();
//
// 		mockMvc.perform(post("/api/admin/coupons")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isCreated())
// 			.andExpect(jsonPath("$.status").value("success"));
//
// 		verify(couponService).registerCoupon(any());
// 	}
//
// 	@Test
// 	@DisplayName("쿠폰 전체 조회 - 성공")
// 	@WithMockUser
// 	void getAllCoupons_success() throws Exception {
// 		Pageable pageable = PageRequest.of(0, 10);
// 		List<CouponResponse> content = List.of();
// 		Page<CouponResponse> page = new PageImpl<>(content, pageable, 0);
//
// 		given(couponService.getAllCoupons(any())).willReturn(page);
//
// 		mockMvc.perform(get("/api/admin/coupons")
// 				.param("page", "0")
// 				.param("size", "10"))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value("success"))
// 			.andExpect(jsonPath("$.data.content").isArray());
//
// 		verify(couponService).getAllCoupons(any());
// 	}
//
// 	@Test
// 	@DisplayName("쿠폰 수정 - 성공")
// 	@WithMockUser
// 	void updateCoupon_success() throws Exception {
// 		CouponUpdateRequest request = CouponUpdateRequest.builder()
// 			.name("updated")
// 			.bookId(2L)
// 			.build();
//
// 		mockMvc.perform(patch("/api/admin/coupon/{id}", 1L)
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value("success"));
//
// 		verify(couponService).updateCoupon(eq(1L), any());
// 	}
//
// 	@Test
// 	@DisplayName("쿠폰 삭제 - 성공")
// 	@WithMockUser
// 	void deleteCoupon_success() throws Exception {
// 		mockMvc.perform(delete("/api/admin/coupon/{id}", 1L))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value("success"));
//
// 		verify(couponService).deleteCoupon(1L);
// 	}
// }
//
