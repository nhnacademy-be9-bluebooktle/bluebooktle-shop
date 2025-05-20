package shop.bluebooktle.backend.book.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.backend.book.service.BookLikesService;
import shop.bluebooktle.common.dto.book.response.BookLikesResponse;

@WebMvcTest(BookLikesController.class)
public class BookLikesControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookLikesService bookLikesService;

	@Test
	@DisplayName("도서 좋아요 등록 - 성공")
	void registerLikeBook_success() throws Exception {
		mockMvc.perform(post("/api/books/1/likes")
				.param("userId", "1"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));
	}

	@Test
	@DisplayName("도서 좋아요 취소 - 성공")
	void unlikeBook_success() throws Exception {
		mockMvc.perform(delete("/api/books/1/likes")
				.param("userId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}

	@Test
	@DisplayName("좋아요 상태 조회 - 성공")
	void isLiked_success() throws Exception {
		BookLikesResponse response = BookLikesResponse.builder()
			.bookId(1L)
			.isLiked(true)
			.countLikes(5)
			.build();
		given(bookLikesService.isLiked(1L, 2L)).willReturn(response);

		mockMvc.perform(get("/api/books/1/likes/status")
				.param("userId", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.bookId").value(1))
			.andExpect(jsonPath("$.data.liked").value(true))
			.andExpect(jsonPath("$.data.countLikes").value(5));
	}

	@Test
	@DisplayName("도서 좋아요 수 조회 - 성공")
	void countLikes_success() throws Exception {
		BookLikesResponse response = BookLikesResponse.builder()
			.bookId(1L)
			.isLiked(true)
			.countLikes(10)
			.build();
		given(bookLikesService.countLikes(1L)).willReturn(response);

		mockMvc.perform(get("/api/books/1/likes/count"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.countLikes").value(10));
	}

	@Test
	@DisplayName("좋아요 목록 조회 - 성공")
	void getBooksLiked_success() throws Exception {
		BookLikesResponse response1 = BookLikesResponse.builder()
			.bookId(1L)
			.isLiked(true)
			.countLikes(5)
			.build();
		BookLikesResponse response2 = BookLikesResponse.builder()
			.bookId(3L)
			.isLiked(true)
			.countLikes(8)
			.build();
		List<BookLikesResponse> list = List.of(response1, response2);
		given(bookLikesService.getBooksLikedByUser(5L)).willReturn(list);

		mockMvc.perform(get("/api/books/likes")
				.param("userId", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data[0].bookId").value(1))
			.andExpect(jsonPath("$.data[1].bookId").value(3));
	}
}
