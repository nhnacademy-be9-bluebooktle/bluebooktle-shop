package shop.bluebooktle.frontend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;

@WebMvcTest(controllers = MainController.class)
@TestPropertySource(properties = {
	"server.gateway-url=http://localhost:8080",
	"minio.endpoint=http://localhost:9000"
})
class MainControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookService bookService;

	@Autowired
	private CategoryService categoryService;

	@TestConfiguration
	static class MockConfig {
		@Bean
		public BookService bookService() {
			return mock(BookService.class);
		}

		@Bean
		public CategoryService categoryService() {
			return mock(CategoryService.class);
		}

		@Bean
		public CartService cartService() {
			return mock(CartService.class);
		}
	}

	@Test
	@DisplayName("메인 페이지 요청 성공")
	void mainPage_success() throws Exception {
		// given
		Long categoryId = 100L;
		CategoryResponse categoryResponse = new CategoryResponse(categoryId, "베스트셀러", null, "/100");

		BookInfoResponse bookInfo = BookInfoResponse.builder()
			.bookId(1L)
			.title("테스트 도서")
			.authors(List.of("홍길동"))
			.salePrice(new BigDecimal("12000"))
			.price(new BigDecimal("15000"))
			.imgUrl("/img/sample.png")
			.createdAt(LocalDateTime.now())
			.star(new BigDecimal("4.5"))
			.reviewCount(10L)
			.viewCount(100L)
			.build();

		Page<BookInfoResponse> dummyPage = new PageImpl<>(List.of(bookInfo));

		when(categoryService.getCategoryByName("베스트셀러")).thenReturn(categoryResponse);
		when(bookService.getPagedBooksByCategoryId(0, 10, BookSortType.NEWEST, categoryId)).thenReturn(dummyPage);
		when(bookService.getCategoryById(categoryId)).thenReturn(categoryResponse);
		when(bookService.getPagedBooks(0, 10, "", BookSortType.NEWEST)).thenReturn(dummyPage);

		// when & then
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("main"))
			.andExpect(model().attributeExists("hotBooks"))
			.andExpect(model().attributeExists("hotCategory"))
			.andExpect(model().attributeExists("newBooks"));

		verify(categoryService).getCategoryByName("베스트셀러");
		verify(bookService).getPagedBooksByCategoryId(0, 10, BookSortType.NEWEST, categoryId);
		verify(bookService).getCategoryById(categoryId);
		verify(bookService).getPagedBooks(0, 10, "", BookSortType.NEWEST);
	}

}
