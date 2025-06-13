package shop.bluebooktle.frontend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;

@WebMvcTest(controllers = CartController.class,
	excludeAutoConfiguration = {
		org.springframework.cloud.openfeign.FeignAutoConfiguration.class
	})
@TestPropertySource(properties = {
	"server.gateway-url=http://localhost:8080",
	"minio.endpoint=http://localhost:9000"  // 더미값
})
class CartControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CartService cartService;

	private final String guestId = "guest-abc123";
	private final Cookie guestIdCookie = new Cookie("GUEST_ID", guestId);

	@TestConfiguration
	static class MockConfig {
		@Bean
		public CartService cartService() {
			return mock(CartService.class);
		}

		@Bean
		public CategoryService categoryService() {
			return mock(CategoryService.class);
		}
	}

	@Test
	@DisplayName("장바구니 담기 성공")
	void addToCart_success() throws Exception {
		mockMvc.perform(post("/cart")
				.param("bookId", "1")
				.param("quantity", "2")
				.cookie(guestIdCookie))
			.andExpect(status().isOk())
			.andExpect(content().string("ok"));

		verify(cartService).addToCart(guestId, 1L, 2);
	}

	@Test
	@DisplayName("장바구니 GUEST_ID 없으면 예외 발생")
	void addToCart_missingGuestId() throws Exception {
		mockMvc.perform(post("/cart")
				.param("bookId", "1")
				.param("quantity", "2"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@DisplayName("장바구니 목록 조회")
	void getCartItems() throws Exception {
		List<BookCartOrderResponse> dummyList = List.of(
			new BookCartOrderResponse(
				1L,
				"테스트 도서",
				new BigDecimal("15000"),
				new BigDecimal("12000"),
				"/img/sample.png",
				List.of("소설", "베스트셀러"),
				true,
				2
			)
		);

		when(cartService.getCartItems(guestId)).thenReturn(dummyList);

		mockMvc.perform(get("/cart").cookie(guestIdCookie))
			.andExpect(status().isOk())
			.andExpect(view().name("cart/cart"))
			.andExpect(model().attributeExists("cartItems"));

		verify(cartService).getCartItems(guestId);
	}

	@Test
	@DisplayName("수량 증가")
	void increaseQuantity() throws Exception {
		mockMvc.perform(post("/cart/increase")
				.param("bookId", "1")
				.cookie(guestIdCookie))
			.andExpect(status().isOk());

		verify(cartService).increaseQuantity(guestId, 1L);
	}

	@Test
	@DisplayName("수량 감소")
	void decreaseQuantity() throws Exception {
		mockMvc.perform(post("/cart/decrease")
				.param("bookId", "1")
				.cookie(guestIdCookie))
			.andExpect(status().isOk());

		verify(cartService).decreaseQuantity(guestId, 1L);
	}

	@Test
	@DisplayName("단일 항목 삭제")
	void removeOne() throws Exception {
		mockMvc.perform(delete("/cart")
				.param("bookId", "1")
				.cookie(guestIdCookie))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/cart"));

		verify(cartService).removeOne(guestId, 1L);
	}

	@Test
	@DisplayName("선택 항목 삭제")
	void removeSelected() throws Exception {
		mockMvc.perform(delete("/cart/selected")
				.param("bookIds", "1", "2")
				.cookie(guestIdCookie))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/cart"));

		verify(cartService).removeSelected(eq(guestId), eq(List.of(1L, 2L)));
	}
}
