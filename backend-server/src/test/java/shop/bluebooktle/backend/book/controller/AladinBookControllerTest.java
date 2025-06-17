package shop.bluebooktle.backend.book.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

import shop.bluebooktle.backend.book.service.AladinBookService;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(AladinBookController.class)
@ActiveProfiles("test")
class AladinBookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@MockitoBean
	private AladinBookService aladinBookService;

	@MockitoBean
	private BookRegisterService bookRegisterService;

	@Test
	@DisplayName("알라딘 API 도서 검색 - 성공")
	@WithMockUser(roles = "ADMIN")
	void searchBooks_Success() throws Exception {
		String keyword = "Effective Java";
		int page = 1;
		int size = 10;

		AladinBookResponse bookResponse = AladinBookResponse.builder()
			.title("Effective Java")
			.author("Joshua Bloch")
			.description("A must-read for Java developers.")
			.publishDate(LocalDateTime.of(2018, 1, 1, 0, 0, 0))
			.isbn("978-0134685991")
			.price(BigDecimal.valueOf(30000))
			.salePrice(BigDecimal.valueOf(27000))
			.salePercentage(BigDecimal.valueOf(10))
			.publisher("Addison-Wesley")
			.categoryName("Programming")
			.imgUrl("http://example.com/cover.jpg")
			.build();
		List<AladinBookResponse> expectedResponse = Collections.singletonList(bookResponse);

		when(aladinBookService.searchBooks(keyword, page, size))
			.thenReturn(expectedResponse);

		mockMvc.perform(get("/api/aladin/books/aladin-search")
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data[0].title").value("Effective Java"))
			.andExpect(jsonPath("$.data[0].author").value("Joshua Bloch"))
			.andExpect(jsonPath("$.data[0].isbn").value("978-0134685991"));

		verify(aladinBookService, times(1)).searchBooks(keyword, page, size);
	}

	@Test
	@DisplayName("알라딘 API 도서 검색 - 결과 없음")
	@WithMockUser(roles = "ADMIN")
	void searchBooks_NoResults() throws Exception {
		String keyword = "NonExistentBook";
		int page = 1;
		int size = 10;


		when(aladinBookService.searchBooks(keyword, page, size))
			.thenReturn(Collections.emptyList());


		mockMvc.perform(get("/api/aladin/books/aladin-search")
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isEmpty());

		verify(aladinBookService, times(1)).searchBooks(keyword, page, size);
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 - 성공")
	@WithMockUser(roles = "ADMIN")
	void registerAladinBook_Success() throws Exception {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("9781234567890")
			.index("sample_index")
			.stock(10)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.categoryIdList(Arrays.asList(1L, 2L))
			.tagIdList(Arrays.asList(101L, 102L))
			.build();

		doNothing().when(bookRegisterService).registerBookByAladin(any(BookAllRegisterByAladinRequest.class));

		mockMvc.perform(post("/api/aladin/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").doesNotExist());

		verify(bookRegisterService, times(1)).registerBookByAladin(any(BookAllRegisterByAladinRequest.class));
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 - 유효성 검사 실패 (ISBN 누락)")
	@WithMockUser(roles = "ADMIN")
	void registerAladinBook_ValidationFailure_BlankIsbn() throws Exception {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("")
			.index("sample_index")
			.stock(10)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.categoryIdList(Arrays.asList(1L))
			.build();

		mockMvc.perform(post("/api/aladin/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))

			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"));

		verify(bookRegisterService, never()).registerBookByAladin(any(BookAllRegisterByAladinRequest.class));
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 - 유효성 검사 실패 (ISBN 길이 불일치)")
	@WithMockUser(roles = "ADMIN")
	void registerAladinBook_ValidationFailure_InvalidIsbnSize() throws Exception {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("12345")
			.index("sample_index")
			.stock(10)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.categoryIdList(Arrays.asList(1L))
			.build();

		mockMvc.perform(post("/api/aladin/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.isbn").value("isbn은 13자여야 합니다"));

		verify(bookRegisterService, never()).registerBookByAladin(any(BookAllRegisterByAladinRequest.class));
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 - 유효성 검사 실패 (재고 음수)")
	@WithMockUser(roles = "ADMIN")
	void registerAladinBook_ValidationFailure_NegativeStock() throws Exception {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("9781234567890")
			.stock(-5)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.categoryIdList(Arrays.asList(1L))
			.build();

		mockMvc.perform(post("/api/aladin/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))

			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.stock").value("재고는 0 이상이어야 합니다."));

		verify(bookRegisterService, never()).registerBookByAladin(any(BookAllRegisterByAladinRequest.class));
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 - 유효성 검사 실패 (판매 상태 누락)")
	@WithMockUser(roles = "ADMIN")
	void registerAladinBook_ValidationFailure_NullSaleState() throws Exception {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("9781234567890")
			.stock(10)
			.isPackable(true)
			.state(null)
			.categoryIdList(Arrays.asList(1L))
			.build();

		mockMvc.perform(post("/api/aladin/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.state").value("판매 상태를 입력해주세요."));

		verify(bookRegisterService, never()).registerBookByAladin(any(BookAllRegisterByAladinRequest.class));
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 - 유효성 검사 실패 (카테고리 ID 목록 비어 있음)")
	@WithMockUser(roles = "ADMIN")
	void registerAladinBook_ValidationFailure_EmptyCategoryIdList() throws Exception {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("9781234567890")
			.stock(10)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.categoryIdList(Collections.emptyList())
			.build();

		mockMvc.perform(post("/api/aladin/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.categoryIdList").value("카테고리는 최소 1개 이상 필요합니다."));

		verify(bookRegisterService, never()).registerBookByAladin(any(BookAllRegisterByAladinRequest.class));
	}
}