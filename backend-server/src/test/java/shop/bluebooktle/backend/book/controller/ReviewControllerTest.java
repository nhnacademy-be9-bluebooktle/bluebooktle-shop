package shop.bluebooktle.backend.book.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.ReviewService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.book.request.ReviewRegisterRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;
import shop.bluebooktle.common.exception.book.ReviewAuthorizationException;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(ReviewController.class)
@ActiveProfiles("test")
class ReviewControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ReviewService reviewService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	private UserPrincipal testUserPrincipal = new UserPrincipal(
		UserDto.builder()
			.id(1L)
			.loginId("testuser")
			.nickname("테스트닉네임")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build()
	);

	@Test
	@DisplayName("리뷰 등록 성공")
	@WithMockUser
	void addReview_success() throws Exception {
		Long bookOrderId = 1L;
		ReviewRegisterRequest request = ReviewRegisterRequest.builder()
			.star(5)
			.reviewContent("정말 좋았던 책입니다! 강추해요.")
			.imgUrls(List.of("http://example.com/img1.jpg"))
			.build();

		ReviewResponse expectedResponse = ReviewResponse.builder()
			.reviewId(10L)
			.userId(testUserPrincipal.getUserId())
			.bookOrderId(bookOrderId)
			.bookTitle("책의 제목입니다")
			.nickname(testUserPrincipal.getNickname())
			.imgUrl("http://example.com/review_thumb.jpg")
			.star(5)
			.reviewContent("정말 좋았던 책입니다! 강추해요.")
			.likes(0)
			.createdAt(LocalDateTime.now())
			.build();

		given(reviewService.addReview(anyLong(), eq(bookOrderId), any(ReviewRegisterRequest.class)))
			.willReturn(expectedResponse);

		mockMvc.perform(post("/api/orders/reviews/{bookOrderId}", bookOrderId)
				.with(user(testUserPrincipal))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.reviewId").value(10L))
			.andExpect(jsonPath("$.data.userId").value(testUserPrincipal.getUserId()))
			.andExpect(jsonPath("$.data.nickname").value(testUserPrincipal.getNickname()))
			.andExpect(jsonPath("$.data.bookTitle").value("책의 제목입니다"))
			.andExpect(jsonPath("$.data.reviewContent").value("정말 좋았던 책입니다! 강추해요."));
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 주문자와 리뷰 작성자 불일치")
	@WithMockUser
	void addReview_failure_authorizationFailed() throws Exception {
		Long bookOrderId = 1L;
		ReviewRegisterRequest request = ReviewRegisterRequest.builder()
			.star(5)
			.reviewContent("이 책은 정말 좋은 책입니다.")
			.build();

		given(reviewService.addReview(anyLong(), eq(bookOrderId), any(ReviewRegisterRequest.class)))
			.willThrow(new ReviewAuthorizationException());

		mockMvc.perform(post("/api/orders/reviews/{bookOrderId}", bookOrderId)
				.with(user(testUserPrincipal))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.message").exists());
	}

	@Test
	@DisplayName("도서 리뷰 조회 성공")
	@WithMockUser
	void getReviewsForBook_success() throws Exception {
		Long bookId = 10L;
		List<ReviewResponse> reviewList = List.of(
			ReviewResponse.builder()
				.reviewId(3L).userId(2L).bookOrderId(200L)
				.bookTitle("도서 A").nickname("익명독자1")
				.star(4).reviewContent("도서 A에 대한 리뷰입니다 1").likes(7).createdAt(LocalDateTime.now().minusDays(3))
				.build(),
			ReviewResponse.builder()
				.reviewId(4L).userId(3L).bookOrderId(201L)
				.bookTitle("도서 A").nickname("익명독자2")
				.star(5).reviewContent("도서 A에 대한 리뷰입니다 2").likes(12).createdAt(LocalDateTime.now().minusDays(2))
				.build()
		);
		PageImpl<ReviewResponse> pageResult = new PageImpl<>(reviewList, PageRequest.of(0, 5), reviewList.size());

		given(reviewService.getReviewsForBook(eq(bookId), any(PageRequest.class)))
			.willReturn(pageResult);

		mockMvc.perform(get("/api/orders/reviews/book/{bookId}", bookId)
				.param("page", "0")
				.param("size", "5"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].reviewId").value(3L))
			.andExpect(jsonPath("$.data.content[0].bookTitle").value("도서 A"))
			.andExpect(jsonPath("$.data.content[1].reviewId").value(4L))
			.andExpect(jsonPath("$.data.totalElements").value(2));
	}

	@Test
	@DisplayName("도서 리뷰 조회 - 리뷰 없는경우")
	@WithMockUser
	void getReviewsForBook_noReviews() throws Exception {
		Long bookId = 10L;
		PageImpl<ReviewResponse> pageResult = new PageImpl<>(List.of(), PageRequest.of(0, 5), 0);

		given(reviewService.getReviewsForBook(eq(bookId), any(PageRequest.class)))
			.willReturn(pageResult);

		mockMvc.perform(get("/api/orders/reviews/book/{bookId}", bookId)
				.param("page", "0")
				.param("size", "5"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content").isEmpty())
			.andExpect(jsonPath("$.data.totalElements").value(0));
	}

	@Test
	@DisplayName("리뷰 좋아요")
	@WithMockUser
	void toggleReviewLike_success_addLike() throws Exception {
		Long reviewId = 1L;
		given(reviewService.toggleReviewLike(eq(reviewId), eq(testUserPrincipal.getUserId())))
			.willReturn(true);

		mockMvc.perform(post("/api/orders/reviews/{reviewId}/like", reviewId)
				.with(user(testUserPrincipal))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value(true));
	}

	@Test
	@DisplayName("리뷰 좋아요 취소")
	@WithMockUser
	void toggleReviewLike_success_removeLike() throws Exception {
		Long reviewId = 1L;
		given(reviewService.toggleReviewLike(eq(reviewId), eq(testUserPrincipal.getUserId())))
			.willReturn(false);

		mockMvc.perform(post("/api/orders/reviews/{reviewId}/like", reviewId)
				.with(user(testUserPrincipal))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value(false));
	}
}