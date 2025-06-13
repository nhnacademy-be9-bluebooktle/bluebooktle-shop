package shop.bluebooktle.frontend.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.frontend.service.AdminPointService;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;
import shop.bluebooktle.frontend.service.DeliveryRuleService;
import shop.bluebooktle.frontend.service.ReviewService;

@WebMvcTest(BookController.class)
@ActiveProfiles("test")
public class BookControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	BookService bookService;
	@MockitoBean
	DeliveryRuleService deliveryRuleService;
	@MockitoBean
	ReviewService reviewService;
	@MockitoBean
	AdminPointService adminPointService;
	@MockitoBean
	CartService cartService;
	@MockitoBean
	CategoryService categoryService;

	@Test
	@DisplayName("도서 찜 등록 성공")
	void likeBook_success() throws Exception {
		// given
		long bookId = 1L;
		willDoNothing().given(bookService).like(bookId);

		// when & then
		mockMvc.perform(get("/books/{bookId}/likes", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId))
			.andExpect(flash().attribute("globalSuccessMessage", "찜 완료!"));

		then(bookService).should().like(bookId);
	}

	@Test
	@DisplayName("도서 찜 등록 실패")
	void likeBook_failure() throws Exception {
		long bookId = 1L;
		willThrow(new RuntimeException("fail")).given(bookService).like(bookId);

		mockMvc.perform(get("/books/{bookId}/likes", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId))
			.andExpect(flash().attributeExists("globalErrorMessage"));

		then(bookService).should().like(bookId);
	}

	@Test
	@DisplayName("도서 찜 취소 성공")
	void unlikeBook_success() throws Exception {
		long bookId = 1L;
		willDoNothing().given(bookService).unlike(bookId);

		mockMvc.perform(post("/books/{bookId}/unlikes", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId))
			.andExpect(flash().attribute("globalSuccessMessage", "찜 취소!"));

		then(bookService).should().unlike(bookId);
	}

	@Test
	@DisplayName("도서 찜 취소 실패")
	void unlikeBook_failure() throws Exception {
		long bookId = 1L;
		willThrow(new RuntimeException("fail")).given(bookService).unlike(bookId);

		mockMvc.perform(post("/books/{bookId}/unlikes", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId))
			.andExpect(flash().attributeExists("globalErrorMessage"));

		then(bookService).should().unlike(bookId);
	}
}
