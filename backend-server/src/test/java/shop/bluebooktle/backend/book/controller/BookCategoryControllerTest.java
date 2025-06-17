package shop.bluebooktle.backend.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.BookCategoryService;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(controllers = BookCategoryController.class,
	excludeAutoConfiguration = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class
	}
)
@ActiveProfiles("test")
class BookCategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private BookCategoryService bookCategoryService;

	@MockitoBean
	private JwtUtil jwtUtil;
  
	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("카테고리 도서 조회")
	@WithMockUser
	void getBooksByCategory() throws Exception {
		// given
		Long categoryId = 1L;
		BookSortType sortType = BookSortType.NEWEST;
		List<BookInfoResponse> books = List.of(
			new BookInfoResponse(1L, "도서 제목1", List.of("저자1"), new BigDecimal(9000), new BigDecimal(10000), "image1", LocalDateTime.now(), new BigDecimal(5), 1L, 1L),
			new BookInfoResponse(2L, "도서 제목2", List.of("저자2"), new BigDecimal(18000), new BigDecimal(20000), "image2", LocalDateTime.now(), new BigDecimal(5), 1L, 1L)
		);
		Page<BookInfoResponse> bookPage = new PageImpl<>(books, PageRequest.of(0, 10), books.size());

		when(bookCategoryService.searchBooksByCategory(eq(categoryId), any(), eq(sortType)))
			.thenReturn(bookPage);

		// when & then
		mockMvc.perform(get("/api/categories/{categoryId}/books", categoryId)
				.param("bookSortType", sortType.name())
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content.length()").value(books.size()))
			.andExpect(jsonPath("$.data.content[0].bookId").value(books.getFirst().bookId()))
			.andExpect(jsonPath("$.data.content[0].title").value(books.getFirst().title()));

		verify(bookCategoryService, times(1)).searchBooksByCategory(eq(categoryId), any(), eq(sortType));
	}

}
