package shop.bluebooktle.backend.book.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.service.BookSaleInfoService;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoResponse;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoUpdateResponse;
import shop.bluebooktle.common.service.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(BookSaleInfoController.class)
class BookSaleInfoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private BookSaleInfoService bookSaleInfoService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("도서 판매정보 등록 성공")
	@WithMockUser
	void registerBookSaleInfo_Success() throws Exception {
		// Given
		BookSaleInfoRegisterRequest request = BookSaleInfoRegisterRequest.builder()
			.bookId(1L)
			.price(BigDecimal.valueOf(20000))
			.salePrice(BigDecimal.valueOf(18000))
			.stock(50)
			.isPackable(true)
			.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
			.build();

		BookSaleInfoRegisterResponse response = BookSaleInfoRegisterResponse.builder()
			.id(1L)
			.title("Test Book")
			.price(BigDecimal.valueOf(20000))
			.salePrice(BigDecimal.valueOf(18000))
			.salePercentage(BigDecimal.valueOf(10))
			.stock(50)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.build();

		Mockito.when(bookSaleInfoService.save(any(BookSaleInfoRegisterRequest.class)))
			.thenReturn(response);

		// When & Then
		mockMvc.perform(post("/api/book-sale-infos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)).with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.title").value("Test Book"));
	}

	@Test
	@DisplayName("도서 판매정보 조회 성공")
	@WithMockUser
	void getBookSaleInfoById_Success() throws Exception {
		// Given
		Long id = 1L;

		Book book = Book.builder()
			.id(1L)
			.title("Test Book")
			.build();

		BookSaleInfo bookSaleInfo = BookSaleInfo.builder()
			.id(id)
			.book(book)
			.price(BigDecimal.valueOf(20000))
			.salePrice(BigDecimal.valueOf(18000))
			.salePercentage(BigDecimal.valueOf(10))
			.stock(50)
			.isPackable(true)
			.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
			.build();

		// BookSaleInfoResponse 객체 생성
		BookSaleInfoResponse response = BookSaleInfoResponse.builder()
			.id(bookSaleInfo.getId())
			.title(bookSaleInfo.getBook().getTitle())
			.price(bookSaleInfo.getPrice())
			.salePrice(bookSaleInfo.getSalePrice())
			.salePercentage(bookSaleInfo.getSalePercentage())
			.stock(bookSaleInfo.getStock())
			.isPackable(bookSaleInfo.isPackable())
			.state(bookSaleInfo.getBookSaleInfoState())
			.build();

		// Mock 설정
		Mockito.when(bookSaleInfoService.findById(eq(id)))
			.thenReturn(response);

		// When & Then
		mockMvc.perform(get("/api/book-sale-infos/{id}", id)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(response.getId()))
			.andExpect(jsonPath("$.data.title").value(response.getTitle()))
			.andExpect(jsonPath("$.data.price").value(response.getPrice().intValue())) // BigDecimal 값을 단순 숫자로 매칭
			.andExpect(jsonPath("$.data.salePrice").value(response.getSalePrice().intValue()))
			.andExpect(jsonPath("$.data.salePercentage").value(response.getSalePercentage().intValue()))
			.andExpect(jsonPath("$.data.stock").value(response.getStock()))
			.andExpect(jsonPath("$.data.isPackable").value(response.getIsPackable()))
			.andExpect(jsonPath("$.data.state").value(response.getState().name()));
	}

	@Test
	@DisplayName("도서 판매정보 수정 성공")
	@WithMockUser
	void updateBookSaleInfo_Success() throws Exception {
		// Given
		Long id = 1L;

		BookSaleInfoUpdateRequest request = BookSaleInfoUpdateRequest.builder()
			.bookId(1L)
			.price(BigDecimal.valueOf(22000))
			.salePrice(BigDecimal.valueOf(20000))
			.stock(40)
			.isPackable(false)
			.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
			.build();

		BookSaleInfoUpdateResponse response = BookSaleInfoUpdateResponse.builder()
			.id(1L)
			.title("Updated Book")
			.price(BigDecimal.valueOf(22000))
			.salePrice(BigDecimal.valueOf(20000))
			.salePercentage(BigDecimal.valueOf(9.1))
			.stock(40)
			.isPackable(false)
			.state(BookSaleInfoState.AVAILABLE)
			.build();

		Mockito.when(bookSaleInfoService.update(eq(id), any(BookSaleInfoUpdateRequest.class)))
			.thenReturn(response);

		// When & Then
		mockMvc.perform(put("/api/book-sale-infos/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.title").value("Updated Book"));
	}

	@Test
	@DisplayName("도서 판매정보 삭제 성공")
	@WithMockUser
	void deleteBookSaleInfo_Success() throws Exception {
		// Given
		Long id = 1L;

		Mockito.doNothing().when(bookSaleInfoService).deleteById(eq(id));

		// When & Then
		mockMvc.perform(delete("/api/book-sale-infos/{id}", id)
				.contentType(MediaType.APPLICATION_JSON).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}
}