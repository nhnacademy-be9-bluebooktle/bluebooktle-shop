package shop.bluebooktle.frontend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;

@WebMvcTest(controllers = CartConversionController.class)
@TestPropertySource(properties = {
	"server.gateway-url=http://localhost:8080",
	"minio.endpoint=http://localhost:9000"
})
class CartConversionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CartService cartService;

	private final String guestId = "guest-xyz789";
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
	@DisplayName("게스트 장바구니 병합/전환 성공")
	void mergeOrConvert_success() throws Exception {
		mockMvc.perform(get("/merge").cookie(guestIdCookie))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));

		verify(cartService).mergeOrConvertGuestCart(guestId);
	}

	@Test
	@DisplayName("게스트 ID 없이 병합 요청 시 null로 처리")
	void mergeOrConvert_withoutGuestId() throws Exception {
		mockMvc.perform(get("/merge"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));

		verify(cartService).mergeOrConvertGuestCart(null);
	}
}
