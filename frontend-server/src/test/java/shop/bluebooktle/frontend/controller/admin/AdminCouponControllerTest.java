package shop.bluebooktle.frontend.controller.admin;

import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.AdminBookService;
import shop.bluebooktle.frontend.service.AdminCategoryService;
import shop.bluebooktle.frontend.service.AdminCouponService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;

@WebMvcTest(controllers = AdminCouponController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalUserInfoAdvice.class))
@ActiveProfiles("test")
class AdminCouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminCouponService adminCouponService;

	@MockitoBean
	private AdminCategoryService adminCategoryService;

	@MockitoBean
	private AdminBookService adminBookService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CartService cartService;
	@MockitoBean
	private CategoryService categoryService;

	@Test
	@DisplayName("GET /admin/coupons/type/new")
	void showCouponTypeForm() throws Exception {
		mockMvc.perform(get("/admin/coupons/type/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/coupon/coupon_type_form"))
			.andExpect(model().attributeExists("couponType"));
	}

	@Test
	@DisplayName("POST /admin/coupons/type")
	void registerCouponType() throws Exception {
		CouponTypeRegisterRequest request = CouponTypeRegisterRequest.builder()
			.name("정책1")
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.valueOf(10000))
			.build();

		mockMvc.perform(post("/admin/coupons/type")
				.flashAttr("couponType", request))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/coupons"));

		then(adminCouponService).should().registerCouponType(request);
	}

	@Test
	@DisplayName("GET /admin/coupons/new")
	void showCouponForm() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		List<CouponTypeResponse> content = List.of(new CouponTypeResponse());
		PageImpl<CouponTypeResponse> page = new PageImpl<>(content, pageable, content.size());
		PaginationData<CouponTypeResponse> couponTypeData = new PaginationData<>(page);

		given(adminCouponService.getAllCouponType(any(Pageable.class))).willReturn(couponTypeData);
		given(adminBookService.getPagedBooksByAdmin(anyInt(), anyInt(), any())).willReturn(new PageImpl<>(List.of()));
		given(adminCategoryService.getCategoryTree()).willReturn(List.of());

		mockMvc.perform(get("/admin/coupons/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/coupon/coupon_form"))
			.andExpect(model().attributeExists("coupon"))
			.andExpect(model().attributeExists("couponTypeData"));
	}

	@Test
	@DisplayName("POST /admin/coupons")
	void registerCoupon() throws Exception {
		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(1L).name("쿠폰명").build();

		mockMvc.perform(post("/admin/coupons")
				.flashAttr("coupon", request))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/coupons"));

		then(adminCouponService).should().registerCoupon(request);
	}

	@Test
	@DisplayName("GET /admin/coupons")
	void getAllCoupon() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);

		CouponResponse coupon = CouponResponse.builder()
			.id(1L)
			.couponName("테스트쿠폰")
			.target(CouponTypeTarget.ORDER)
			.build();

		List<CouponResponse> content = List.of(coupon);
		PageImpl<CouponResponse> page = new PageImpl<>(content, pageable, content.size());
		PaginationData<CouponResponse> couponData = new PaginationData<>(page);

		given(adminCouponService.getAllCoupon(any(Pageable.class), any()))
			.willReturn(couponData);

		mockMvc.perform(get("/admin/coupons"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/coupon/coupon_list"))
			.andExpect(model().attributeExists("coupons"));
	}

	@Test
	@DisplayName("GET /admin/coupons - error true")
	void getAllCoupon_error_true() throws Exception {
		CouponResponse coupon = CouponResponse.builder()
			.id(1L)
			.couponName("에러쿠폰")
			.target(CouponTypeTarget.ORDER)
			.build();

		PageImpl<CouponResponse> page = new PageImpl<>(List.of(coupon));
		PaginationData<CouponResponse> couponData = new PaginationData<>(page);

		given(adminCouponService.getAllCoupon(any(Pageable.class), any()))
			.willReturn(couponData);

		mockMvc.perform(get("/admin/coupons")
				.flashAttr("error", "true")
				.flashAttr("errorCode", "ERR001")
				.flashAttr("errorMessage", "테스트 에러 메시지"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/coupon/coupon_list"))
			.andExpect(model().attributeExists("error"))
			.andExpect(model().attribute("error", true))
			.andExpect(model().attribute("errorCode", "ERR001"))
			.andExpect(model().attribute("errorMessage", "테스트 에러 메시지"));
	}

	@Test
	@DisplayName("GET /admin/coupons - issueCouponId != null")
	void getAllCoupon_issueCouponId_notNull() throws Exception {
		CouponResponse coupon = CouponResponse.builder()
			.id(99L)
			.couponName("발급대상쿠폰")
			.target(CouponTypeTarget.ORDER)
			.build();

		PageImpl<CouponResponse> page = new PageImpl<>(List.of(coupon));
		PaginationData<CouponResponse> couponData = new PaginationData<>(page);

		given(adminCouponService.getAllCoupon(any(Pageable.class), any()))
			.willReturn(couponData);

		mockMvc.perform(get("/admin/coupons")
				.param("issueCouponId", "99"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/coupon/coupon_list"))
			.andExpect(model().attribute("issueCouponId", 99L))
			.andExpect(model().attribute("couponId", 99L));
	}

	@Test
	@DisplayName("GET /admin/coupons - with searchKeyword")
	void getAllCoupon_withSearchKeyword() throws Exception {
		CouponResponse coupon = CouponResponse.builder()
			.id(2L)
			.couponName("검색쿠폰")
			.target(CouponTypeTarget.BOOK)
			.build();

		PageImpl<CouponResponse> page = new PageImpl<>(List.of(coupon));
		PaginationData<CouponResponse> couponData = new PaginationData<>(page);

		given(adminCouponService.getAllCoupon(any(Pageable.class), eq("쿠폰"))).willReturn(couponData);

		mockMvc.perform(get("/admin/coupons")
				.param("searchKeyword", "쿠폰"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/coupon/coupon_list"))
			.andExpect(model().attribute("searchKeyword", "쿠폰"));
	}

	@Test
	@DisplayName("POST /admin/coupons/issue")
	void issueCoupon() throws Exception {
		UserCouponRegisterRequest request = UserCouponRegisterRequest.builder()
			.couponId(1L).availableStartAt(LocalDateTime.now()).availableEndAt(LocalDateTime.now().plusDays(1)).build();

		mockMvc.perform(post("/admin/coupons/issue")
				.flashAttr("userCoupon", request))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/coupons"));

		then(adminCouponService).should().issueCoupon(request);
	}

	@Test
	@DisplayName("GET /admin/coupons/book-fragment")
	void getBookFragment() throws Exception {
		given(adminBookService.getPagedBooksByAdmin(anyInt(), anyInt(), any()))
			.willReturn(new PageImpl<>(List.of()));

		mockMvc.perform(get("/admin/coupons/book-fragment"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/coupon/book_list :: bookList"));
	}

	@Test
	@DisplayName("POST /admin/coupons/type - validation fail")
	void registerCouponType_validationFail() throws Exception {
		CouponTypeRegisterRequest invalidRequest = new CouponTypeRegisterRequest();

		mockMvc.perform(post("/admin/coupons/type")
				.flashAttr("couponType", invalidRequest))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/coupons/type/new"));
	}

	@Test
	@DisplayName("POST /admin/coupons - validation fail")
	void registerCoupon_validationFail() throws Exception {
		CouponRegisterRequest invalidRequest = new CouponRegisterRequest();

		mockMvc.perform(post("/admin/coupons")
				.flashAttr("coupon", invalidRequest))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/coupons/new"));
	}

	@Test
	@DisplayName("POST /admin/coupons/issue - validation fail")
	void issueCoupon_validationFail() throws Exception {
		UserCouponRegisterRequest invalidRequest = UserCouponRegisterRequest.builder().build();

		mockMvc.perform(post("/admin/coupons/issue")
				.flashAttr("userCoupon", invalidRequest))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/admin/coupons?issueCouponId=*"));
	}

	@Test
	@DisplayName("POST /admin/coupons - ApplicationException 처리")
	void registerCoupon_applicationException() throws Exception {
		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.name("예외쿠폰")
			.couponTypeId(1L)
			.build();

		willThrow(new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR))
			.given(adminCouponService)
			.registerCoupon(any());

		mockMvc.perform(post("/admin/coupons")
				.flashAttr("coupon", request))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/coupons"))
			.andExpect(flash().attribute("error", "true"))
			.andExpect(flash().attribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.getCode()))
			.andExpect(flash().attribute("errorMessage", ErrorCode.INTERNAL_SERVER_ERROR.getMessage())); // 또는 .name()
	}

}