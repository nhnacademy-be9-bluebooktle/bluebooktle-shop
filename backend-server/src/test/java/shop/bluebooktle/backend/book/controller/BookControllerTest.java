package shop.bluebooktle.backend.book.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(BookController.class) // BookController만 테스트
@ActiveProfiles("test")
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc; // HTTP 요청을 시뮬레이션

	@Autowired
	private ObjectMapper objectMapper; // JSON 직렬화/역직렬화

	@MockitoBean // 스프링 컨텍스트에 MockBean으로 등록하여 실제 서비스 대신 Mock 사용
	private BookService bookService;

	@MockitoBean // 마찬가지로 MockBean으로 등록
	private BookRegisterService bookRegisterService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	// --- 도서 직접 등록 테스트 ---
	@Test
	@WithMockUser
	@DisplayName("도서 직접 등록 성공 - HTTP 201 Created")
	void registerBook_success() throws Exception {
		// Given
		BookAllRegisterRequest request = BookAllRegisterRequest.builder()
			.isbn("979116050848")
			.title("테스트 도서")
			.description("테스트 도서 설명입니다!!")
			.publishDate(LocalDate.of(2023, 1, 1))
			.index("테스트 목차")
			.price(new BigDecimal("25000.00"))
			.salePrice(new BigDecimal("20000.00"))
			.stock(100)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.authorIdList(Collections.singletonList(1L))
			.publisherIdList(Collections.singletonList(1L))
			.categoryIdList(Collections.singletonList(1L))
			.tagIdList(Collections.singletonList(1L))
			.imgUrl("http://example.com/cover.jpg")
			.build();

		// When & Then
		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(user("admin").roles("ADMIN")) // 사용자 인증 및 역할 부여
				.with(csrf())) // CSRF 토큰 추가
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		// Verify that the service method was called
		verify(bookRegisterService, times(1)).registerBook(any(BookAllRegisterRequest.class));
	}

	@Test
	@DisplayName("도서 삭제 성공 - HTTP 200 OK")
	@WithMockUser(roles = {"ADMIN"})
		// 관리자 권한으로 요청
	void deleteBook_success() throws Exception {
		// Given
		Long bookId = 1L;
		doNothing().when(bookService).deleteBook(bookId);

		// When & Then
		mockMvc.perform(delete("/api/books/{bookId}", bookId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf())) // DELETE 요청에도 CSRF 필요할 수 있음
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		// Verify that the service method was called
		verify(bookService, times(1)).deleteBook(bookId);
	}

	@Test
	@DisplayName("도서 삭제 실패 - 도서를 찾을 수 없음 - HTTP 404 Not Found")
	@WithMockUser(roles = {"ADMIN"})
		// 관리자 권한으로 요청
	void deleteBook_notFound() throws Exception {
		// Given
		Long bookId = 999L;
		doThrow(new BookNotFoundException())
			.when(bookService).deleteBook(bookId);

		// When & Then
		mockMvc.perform(delete("/api/books/{bookId}", bookId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"));

		// Verify that the service method was called
		verify(bookService, times(1)).deleteBook(bookId);
	}
}