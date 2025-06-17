package shop.bluebooktle.backend.book.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.BookLikesService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.common.dto.book.response.BookLikesResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(BookLikesController.class)
@ActiveProfiles("test")
class BookLikesControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private BookLikesService bookLikesService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	private UserPrincipal mockUserPrincipal(Long userId) {
		UserDto userDto = UserDto.builder()
			.id(userId)
			.loginId("test@example.com")
			.nickname("홍길동")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();
		return new UserPrincipal(userDto);
	}

	@Test
	@DisplayName("도서 좋아요 등록 - 성공")
	@WithMockUser
	void registerLikeBook() throws Exception {
		// given
		Long userId = 10L;
		Long bookId = 1L;

		// when
		mockMvc.perform(post("/api/books/{bookId}/likes", bookId)
				.with(csrf())
				.with(user(mockUserPrincipal(userId))))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		// then
		verify(bookLikesService, times(1)).like(bookId, userId);
	}

	@Test
	@DisplayName("도서 좋아요 취소 - 성공")
	@WithMockUser
	void unlikeBook() throws Exception {
		// given
		Long userId = 10L;
		Long bookId = 1L;

		// when
		mockMvc.perform(delete("/api/books/{bookId}/likes", bookId)
				.with(csrf())
				.with(user(mockUserPrincipal(userId))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		// then
		verify(bookLikesService).unlike(bookId, userId);
	}

	@Test
	@DisplayName("도서 좋아요 여부 확인 - 성공")
	@WithMockUser
	void isLiked() throws Exception {
		Long bookId = 1L;
		Long userId = 10L;

		BookLikesResponse response = BookLikesResponse.builder()
			.bookId(bookId)
			.isLiked(true)
			.countLikes(3)
			.build();

		given(bookLikesService.isLiked(bookId, userId)).willReturn(response);

		mockMvc.perform(get("/api/books/{bookId}/likes/status", bookId)
				.with(user(mockUserPrincipal(userId))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.bookId").value(bookId))
			.andExpect(jsonPath("$.data.liked").value(true))
			.andExpect(jsonPath("$.data.countLikes").value(3));
	}

	@Test
	@DisplayName("도서 좋아요 수 확인 - 성공")
	@WithMockUser
	void countLikes() throws Exception {
		Long bookId = 1L;

		BookLikesResponse response = BookLikesResponse.builder()
			.bookId(bookId)
			.isLiked(false)
			.countLikes(9)
			.build();

		given(bookLikesService.countLikes(bookId)).willReturn(response);

		mockMvc.perform(get("/api/books/{bookId}/likes/count", bookId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.countLikes").value(9));
	}

	@Test
	@DisplayName("좋아요한 도서 목록 조회 - 성공")
	@WithMockUser
	void getBooksLiked() throws Exception {
		Long userId = 10L;

		BookLikesListResponse response = BookLikesListResponse.builder()
			.bookId(1L)
			.bookName("테스트 책")
			.authorName(List.of("홍길동", "이몽룡"))
			.imgUrl("http://example.com/image.jpg")
			.price(BigDecimal.valueOf(12300))
			.build();

		given(bookLikesService.getBooksLikedByUser(userId))
			.willReturn(List.of(response));

		mockMvc.perform(get("/api/books/likes")
				.with(user(mockUserPrincipal(userId))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data[0].bookName").value("테스트 책"))
			.andExpect(jsonPath("$.data[0].authorName[0]").value("홍길동"))
			.andExpect(jsonPath("$.data[0].imgUrl").value("http://example.com/image.jpg"))
			.andExpect(jsonPath("$.data[0].price").value(12300));
	}
}
