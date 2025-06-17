package shop.bluebooktle.backend.book.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateServiceRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(BookController.class)
@ActiveProfiles("test")
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private BookService bookService;
	@MockitoBean
	private BookRegisterService bookRegisterService;
	@MockitoBean
	private JwtUtil jwtUtil;
	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@WithMockUser
	@DisplayName("도서 직접 등록 성공")
	void registerBook_success() throws Exception {
		// Given
		BookAllRegisterRequest request = BookAllRegisterRequest.builder()
			.isbn("9791160508488")
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
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		// Verify that the service method was called
		verify(bookRegisterService, times(1)).registerBook(any(BookAllRegisterRequest.class));
	}

	@Test
	@DisplayName("도서 삭제 성공")
	@WithMockUser(roles = {"ADMIN"})
	void deleteBook_success() throws Exception {
		// Given
		Long bookId = 1L;
		doNothing().when(bookService).deleteBook(bookId);

		// When & Then
		mockMvc.perform(delete("/api/books/{bookId}", bookId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(bookService, times(1)).deleteBook(bookId);
	}

	@Test
	@DisplayName("도서 삭제 실패 - 도서를 찾을 수 없음")
	@WithMockUser(roles = {"ADMIN"})
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

		verify(bookService, times(1)).deleteBook(bookId);
	}

	@Test
	@DisplayName("도서 정보 업데이트 성공")
	@WithMockUser(roles = {"ADMIN"})
	void updateBook_success() throws Exception {
		// Given
		Long bookId = 1L;
		BookUpdateServiceRequest request = BookUpdateServiceRequest.builder()
			.title("업데이트된 도서 제목")
			.description("업데이트된 도서 설명")
			.price(new BigDecimal("30000.00"))
			.salePrice(new BigDecimal("25000.00"))
			.stock(150)
			.isPackable(false)
			.authorIdList(List.of(2L))
			.publisherIdList(List.of(2L))
			.categoryIdList(List.of(2L))
			.tagIdList(List.of(2L))
			.state(BookSaleInfoState.AVAILABLE)
			.imgUrl("[http://example.com/updated_cover.jpg](http://example.com/updated_cover.jpg)")
			.build();

		doNothing().when(bookService).updateBook(eq(bookId), any(BookUpdateServiceRequest.class));

		// When & Then
		mockMvc.perform(put("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(bookService, times(1)).updateBook(eq(bookId), any(BookUpdateServiceRequest.class));
	}

	@Test
	@DisplayName("관리자용 도서 상세 조회 성공")
	@WithMockUser(roles = {"ADMIN"})
	void getBookByAdmin_success() throws Exception {
		// Given
		Long bookId = 1L;

		BookAllResponse response = BookAllResponse.builder()
			.id(bookId)
			.title("관리자 도서")
			.description("이 도서는 관리자 테스트용입니다.")
			.publishDate(LocalDateTime.of(2024, 5, 10, 0, 0))
			.index("목차 정보")
			.isbn("1234567890123")
			.price(new BigDecimal("15000"))
			.salePrice(new BigDecimal("12000"))
			.stock(20)
			.salePercentage(new BigDecimal("20.0"))
			.imgUrl("http://example.com/test.jpg")
			.isPackable(true)
			.authors(List.of(new AuthorResponse(1L, "작가A", LocalDateTime.now())))
			.publishers(List.of(new PublisherInfoResponse(1L, "출판사A", LocalDateTime.now())))
			.categories(List.of(new CategoryResponse(1L, "소설", "-", "/1")))
			.tags(List.of(new TagInfoResponse(1L, "감성", LocalDateTime.now())))
			.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
			.viewCount(100L)
			.searchCount(50L)
			.reviewCount(5L)
			.star(new BigDecimal("4.5"))
			.createdAt(LocalDateTime.now())
			.build();

		given(bookService.findBookAllById(bookId)).willReturn(response);

		// When & Then
		mockMvc.perform(get("/api/books/{bookId}/admin", bookId)
				.accept(MediaType.APPLICATION_JSON)
				.with(user("admin").roles("ADMIN")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(bookId))
			.andExpect(jsonPath("$.data.title").value("관리자 도서"))
			.andExpect(jsonPath("$.data.isbn").value("1234567890123"));

		verify(bookService, times(1)).findBookAllById(bookId);
	}

	@Test
	@DisplayName("특정 도서 상세 조회 성공")
	@WithMockUser
	void getBook_success() throws Exception {
		// Given
		Long bookId = 1L;
		BookDetailResponse mockResponse = BookDetailResponse.builder()
			.title("테스트 도서")
			.description("테스트 설명")
			.isbn("979-11-6050-842-8")
			.price(new BigDecimal("25000.00"))
			.salePrice(new BigDecimal("20000.00"))
			.imgUrl("http://example.com/book1.jpg")
			.publishers(Collections.singletonList("출판사1"))
			.authors(Collections.singletonList("작가1"))
			.saleState(BookSaleInfoState.AVAILABLE)
			.index("책 상세 목차입니다.")
			.build();

		given(bookService.findBookById(bookId)).willReturn(mockResponse);

		// When & Then
		mockMvc.perform(get("/api/books/{bookId}", bookId)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.title").value("테스트 도서"));

		verify(bookService, times(1)).findBookById(bookId);
	}

	@Test
	@DisplayName("특정 도서 상세 조회 실패 - 도서를 찾을 수 없음")
	@WithMockUser
	void getBook_notFound() throws Exception {
		// Given
		Long bookId = 999L;
		given(bookService.findBookById(bookId)).willThrow(new BookNotFoundException(String.valueOf(bookId)));

		// When & Then
		mockMvc.perform(get("/api/books/{bookId}", bookId)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"));

		verify(bookService, times(1)).findBookById(bookId);
	}

	@Test
	@DisplayName("도서 목록 조회 (검색 및 정렬) 성공")
	@WithMockUser
	void getPagedBooks_success() throws Exception {
		// Given
		int page = 0;
		int size = 10;
		String searchKeyword = "테스트";
		BookSortType sortType = BookSortType.NEWEST;

		BookInfoResponse book1 = BookInfoResponse.builder()
			.bookId(1L)
			.title("테스트 책1")
			.imgUrl("url1")
			.price(new BigDecimal("10000"))
			.salePrice(new BigDecimal("9000"))
			.build();
		BookInfoResponse book2 = BookInfoResponse.builder()
			.bookId(2L)
			.title("테스트 책2")
			.imgUrl("url2")
			.price(new BigDecimal("20000"))
			.salePrice(new BigDecimal("18000"))
			.build();
		Page<BookInfoResponse> mockPage = new PageImpl<>(List.of(book1, book2), PageRequest.of(page, size), 2);

		given(bookService.findAllBooks(page, size, searchKeyword, sortType)).willReturn(mockPage);

		// When & Then
		mockMvc.perform(get("/api/books")
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.param("searchKeyword", searchKeyword)
				.param("bookSortType", sortType.name())
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].bookId").value(1L))
			.andExpect(jsonPath("$.data.totalElements").value(2));

		verify(bookService, times(1)).findAllBooks(page, size, searchKeyword, sortType);
	}

	@Test
	@DisplayName("도서 목록 조회 (검색 및 정렬) 성공")
	@WithMockUser
	void getPagedBooks_noKeyword_success() throws Exception {
		// Given
		int page = 0;
		int size = 10;
		BookSortType sortType = BookSortType.NEWEST;

		BookInfoResponse book1 = BookInfoResponse.builder()
			.bookId(1L)
			.title("책1")
			.imgUrl("url1")
			.price(new BigDecimal("10000"))
			.salePrice(new BigDecimal("9000"))
			.build();
		Page<BookInfoResponse> mockPage = new PageImpl<>(List.of(book1), PageRequest.of(page, size), 1);

		given(bookService.findAllBooks(eq(page), eq(size), isNull(), eq(sortType))).willReturn(mockPage);

		// When & Then
		mockMvc.perform(get("/api/books")
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.param("bookSortType", sortType.name())
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].bookId").value(1L))
			.andExpect(jsonPath("$.data.totalElements").value(1));

		verify(bookService, times(1)).findAllBooks(eq(page), eq(size), isNull(), eq(sortType));
	}

	@Test
	@DisplayName("관리자 도서 목록 조회 성공")
	@WithMockUser(roles = {"ADMIN"})
	void getPagedBooksByAdmin_success() throws Exception {
		// Given
		int page = 0;
		int size = 10;
		String searchKeyword = "관리자";

		AdminBookResponse adminBook1 = AdminBookResponse.builder()
			.bookId(1L)
			.title("관리자용 책1")
			.isbn("111")
			.stock(50)
			.salePrice(new BigDecimal("8000"))
			.publishDate(LocalDate.now().atStartOfDay())
			.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
			.build();
		Page<AdminBookResponse> mockPage = new PageImpl<>(List.of(adminBook1), PageRequest.of(page, size), 1);

		given(bookService.findAllBooksByAdmin(page, size, searchKeyword)).willReturn(mockPage);
		// When & Then
		mockMvc.perform(get("/api/books/admin")
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.param("searchKeyword", searchKeyword)
				.with(user("admin").roles("ADMIN"))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].bookId").value(1L))
			.andExpect(jsonPath("$.data.totalElements").value(1));

		verify(bookService, times(1)).findAllBooksByAdmin(page, size, searchKeyword);
	}

	@Test
	@DisplayName("관리자 도서 목록 조회 성공 - 검색어 없이")
	@WithMockUser(roles = {"ADMIN"})
	void getPagedBooksByAdmin_noKeyword_success() throws Exception {
		// Given
		int page = 0;
		int size = 10;

		AdminBookResponse adminBook1 = AdminBookResponse.builder()
			.bookId(1L)
			.title("관리자용 책1")
			.isbn("111")
			.stock(50)
			.salePrice(new BigDecimal("8000"))
			.publishDate(LocalDate.now().atStartOfDay())
			.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
			.build();
		Page<AdminBookResponse> mockPage = new PageImpl<>(List.of(adminBook1), PageRequest.of(page, size), 1);

		given(bookService.findAllBooksByAdmin(page, size, null))
			.willReturn(mockPage);


		// When & Then
		mockMvc.perform(get("/api/books/admin")
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.with(user("admin").roles("ADMIN"))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].bookId").value(1L))
			.andExpect(jsonPath("$.data.totalElements").value(1));

		verify(bookService, times(1)).findAllBooksByAdmin(eq(page), eq(size), isNull());
	}

	@Test
	@DisplayName("카트/주문용 단일 도서 정보 조회 성공")
	@WithMockUser
	void getBookCartOrders_success() throws Exception {
		// Given
		Long bookId = 1L;
		int quantity = 3;
		BookCartOrderResponse mockResponse = BookCartOrderResponse.builder()
			.bookId(bookId)
			.title("책1")
			.price(new BigDecimal("10000"))
			.salePrice(new BigDecimal("9000"))
			.thumbnailUrl("url1")
			.isPackable(true)
			.build();

		given(bookService.getBookCartOrder(bookId, quantity)).willReturn(mockResponse);

		// When & Then
		mockMvc.perform(get("/api/books/order/{bookId}", bookId)
				.param("quantity", String.valueOf(quantity))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.bookId").value(bookId))
			.andExpect(jsonPath("$.data.title").value("책1"));

		verify(bookService, times(1)).getBookCartOrder(bookId, quantity);
	}

	@Test
	@DisplayName("카트/주문용 단일 도서 정보 조회 실패 - 도서를 찾을 수 없음")
	@WithMockUser
	void getBookCartOrders_notFound() throws Exception {
		// Given
		Long bookId = 999L;
		int quantity = 1;
		given(bookService.getBookCartOrder(bookId, quantity)).willThrow(new BookNotFoundException(
			String.valueOf(bookId)));

		// When & Then
		mockMvc.perform(get("/api/books/order/{bookId}", bookId)
				.param("quantity", String.valueOf(quantity))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"));

		verify(bookService, times(1)).getBookCartOrder(bookId, quantity);
	}

}