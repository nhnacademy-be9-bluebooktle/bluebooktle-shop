package shop.bluebooktle.backend.book.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.dto.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoUpdateResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.service.BookSaleInfoService;

@WebMvcTest(BookSaleInfoController.class)
class BookSaleInfoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private BookSaleInfoService bookSaleInfoService;

	@Test
	@DisplayName("도서 판매정보 등록 성공")
	void registerBookSaleInfo_Success() throws Exception {
		// Given
		BookSaleInfoRegisterRequest request = BookSaleInfoRegisterRequest.builder()
			.bookId(1L)
			.price(BigDecimal.valueOf(20000))
			.salePrice(BigDecimal.valueOf(18000))
			.stock(50)
			.isPackable(true)
			.state(BookSaleInfo.State.AVAILABLE)
			.build();

		BookSaleInfoRegisterResponse response = BookSaleInfoRegisterResponse.builder()
			.id(1L)
			.title("Test Book")
			.price(BigDecimal.valueOf(20000))
			.salePrice(BigDecimal.valueOf(18000))
			.salePercentage(BigDecimal.valueOf(10))
			.stock(50)
			.isPackable(true)
			.state("AVAILABLE")
			.build();

		Mockito.when(bookSaleInfoService.save(any(BookSaleInfoRegisterRequest.class)))
			.thenReturn(response);

		// When & Then
		mockMvc.perform(post("/api/book-sale-infos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.title").value("Test Book"));
	}

	@Test
	@DisplayName("도서 판매정보 조회 성공")
	void getBookSaleInfoById_Success() throws Exception {
		// Given
		Long id = 1L;

		Book book = new Book();

		BookSaleInfo bookSaleInfo = BookSaleInfo.builder()
			.id(id)
			.book(book)
			.price(BigDecimal.valueOf(20000))
			.salePrice(BigDecimal.valueOf(18000))
			.salePercentage(BigDecimal.valueOf(10))
			.stock(50)
			.isPackable(true)
			.state(BookSaleInfo.State.AVAILABLE)
			.build();

		BookSaleInfoUpdateResponse response = BookSaleInfoUpdateResponse.fromEntity(bookSaleInfo);

		Mockito.when(bookSaleInfoService.findById(eq(id)))
			.thenReturn(bookSaleInfo);

		// When & Then
		mockMvc.perform(get("/api/book-sale-infos/{id}", id)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(1L));
	}

	@Test
	@DisplayName("도서 판매정보 수정 성공")
	void updateBookSaleInfo_Success() throws Exception {
		// Given
		Long id = 1L;

		BookSaleInfoUpdateRequest request = BookSaleInfoUpdateRequest.builder()
			.bookId(1L)
			.price(BigDecimal.valueOf(22000))
			.salePrice(BigDecimal.valueOf(20000))
			.stock(40)
			.isPackable(false)
			.state(BookSaleInfo.State.AVAILABLE)
			.build();

		BookSaleInfoUpdateResponse response = BookSaleInfoUpdateResponse.builder()
			.id(1L)
			.title("Updated Book")
			.price(BigDecimal.valueOf(22000))
			.salePrice(BigDecimal.valueOf(20000))
			.salePercentage(BigDecimal.valueOf(9.1))
			.stock(40)
			.isPackable(false)
			.state("UNAVAILABLE")
			.build();

		Mockito.when(bookSaleInfoService.update(eq(id), any(BookSaleInfoUpdateRequest.class)))
			.thenReturn(response);

		// When & Then
		mockMvc.perform(put("/api/book-sale-infos/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.title").value("Updated Book"));
	}

	@Test
	@DisplayName("도서 판매정보 삭제 성공")
	void deleteBookSaleInfo_Success() throws Exception {
		// Given
		Long id = 1L;

		Mockito.doNothing().when(bookSaleInfoService).deleteById(eq(id));

		// When & Then
		mockMvc.perform(delete("/api/book-sale-infos/{id}", id)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}
}