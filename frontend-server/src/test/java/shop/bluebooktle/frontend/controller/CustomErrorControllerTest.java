package shop.bluebooktle.frontend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.RequestDispatcher;
import shop.bluebooktle.frontend.config.advice.GlobalCartCountAdvice;
import shop.bluebooktle.frontend.config.advice.GlobalCategoryInfoAdvice;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CouponService;

@WebMvcTest(
	controllers = CustomErrorController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalUserInfoAdvice.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalCategoryInfoAdvice.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalCartCountAdvice.class)
	}
)
@Import(RefreshAutoConfiguration.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
	"server.gateway-url=http://localhost:8080",
	"minio.endpoint=http://localhost:9000",
	"toss.client-key=testClientKey"
})
class CustomErrorControllerTest {

	@Autowired
	private MockMvc mockMvc;


	@MockitoBean
	private CouponService couponService;
	@MockitoBean
	private CartService cartService;

	@Test
	@DisplayName("ERROR_STATUS_CODE=404 → error/404 뷰 반환")
	void when404Status_thenReturns404View() throws Exception {
		mockMvc.perform(get("/error")
				.requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value())
			)
			.andExpect(view().name("error/404"));
	}

	@Test
	@DisplayName("ERROR_STATUS_CODE != 404 → error/500 뷰 반환")
	void whenOtherStatus_thenReturns500View() throws Exception {
		mockMvc.perform(get("/error")
				.requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_REQUEST.value())
			)
			.andExpect(view().name("error/500"));
	}

	@Test
	@DisplayName("ERROR_STATUS_CODE 미존재 → error/500 뷰 반환")
	void whenNoStatus_thenReturns500View() throws Exception {
		mockMvc.perform(get("/error"))
			.andExpect(view().name("error/500"));
	}
}