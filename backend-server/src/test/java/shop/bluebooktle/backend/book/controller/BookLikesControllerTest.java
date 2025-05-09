package shop.bluebooktle.backend.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.dto.request.BookLikesRequest;
import shop.bluebooktle.backend.book.dto.response.BookLikesResponse;
import shop.bluebooktle.backend.book.service.BookLikesService;

@WebMvcTest(BookLikesController.class)
public class BookLikesControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookLikesController bookLikesController;

	@MockitoBean
	private BookLikesService bookLikesService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("도서 좋아요 등록 성공")
	void likeBookTest() throws Exception {
		mockMvc.perform(post("/api/book/1/likes")
				.param("userId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}

	@Test
	@DisplayName("도서 좋아요 취소 성공")
	void unlikeBookTest() throws Exception {
		mockMvc.perform(delete("/api/book/1/likes")
				.param("userId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}

	@Test
	@DisplayName("좋아요 여부 확인")
	void isLikedTest() throws Exception {
		when(bookLikesService.isLiked(any(BookLikesRequest.class)))
			.thenReturn(BookLikesResponse.builder()
				.bookId(1L)
				.isLiked(true)
				.countLikes(5)
				.build());

		mockMvc.perform(get("/api/book/1/likes/status")
				.param("userId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.bookId").value(1))
			.andExpect(jsonPath("$.data.liked").value(true)) // Jackson의 기본형 boolean은 is 접두어를 제거한 필드명으로 직렬화
			.andExpect(jsonPath("$.data.countLikes").value(5));
	}

	@Test
	@DisplayName("도서 좋아요 수 확인")
	void countLikesTest() throws Exception {
		when(bookLikesService.countLikes(any(BookLikesRequest.class)))
			.thenReturn(BookLikesResponse.builder()
				.bookId(1L)
				.isLiked(true)
				.countLikes(10)
				.build());

		mockMvc.perform(get("/api/book/1/likes/count"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.countLikes").value(10));
	}

	@Test
	@DisplayName("좋아요 누른 도서 목록 조회")
	void getBooksLikedTest() throws Exception {
		when(bookLikesService.getBooksLikedByUser(any(BookLikesRequest.class)))
			.thenReturn(List.of(
				BookLikesResponse.builder()
					.bookId(1L)
					.isLiked(true)
					.countLikes(5)
					.build(),
				BookLikesResponse.builder()
					.bookId(2L)
					.isLiked(true)
					.countLikes(8)
					.build()
			));

		mockMvc.perform(get("/api/book/likes")
				.param("userId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data[0].bookId").value(1))
			.andExpect(jsonPath("$.data[0].countLikes").value(5))
			.andExpect(jsonPath("$.data[1].bookId").value(2))
			.andExpect(jsonPath("$.data[1].countLikes").value(8));
	}
}
