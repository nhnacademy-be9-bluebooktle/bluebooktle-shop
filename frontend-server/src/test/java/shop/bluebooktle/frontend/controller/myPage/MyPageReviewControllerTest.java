package shop.bluebooktle.frontend.controller.myPage;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.review.request.ReviewRequest;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;
import shop.bluebooktle.frontend.service.ReviewService;

@WebMvcTest(MyPageReviewController.class)
@ActiveProfiles("test")
class MyPageReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReviewService reviewService;

	@MockitoBean
	private CartService cartService;

	@MockitoBean
	private CategoryService categoryService;

	@Autowired
	private ObjectMapper objectMapper;

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
	@DisplayName("리뷰 작성 성공")
	void addReview_Success() throws Exception {
		Long bookOrderId = 10L;
		ReviewRequest reviewRequest = new ReviewRequest(5, "이 책 정말 재미있어요! 추천합니다.", Collections.emptyList());

		doNothing().when(reviewService).addReview(anyLong(), anyLong(), any(ReviewRequest.class));

		mockMvc.perform(post("/mypage/reviews/{bookOrderId}", bookOrderId)
				.with(user(testUserPrincipal))
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("reviewContent", reviewRequest.getReviewContent())
				.param("star", String.valueOf(reviewRequest.getStar())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage/reviews"))
			.andExpect(flash().attributeExists("globalSuccessMessage"));

	}

	@Test
	@DisplayName("리뷰 작성 실패 - 유효성 검사 오류")
	void addReview_Failure_Validation() throws Exception {
		Long bookOrderId = 10L;
		ReviewRequest invalidReviewRequest = new ReviewRequest(0, "짧아요", Collections.emptyList());

		mockMvc.perform(post("/mypage/reviews/{bookOrderId}", bookOrderId)
				.with(user(testUserPrincipal))
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("reviewContent", invalidReviewRequest.getReviewContent())
				.param("star", String.valueOf(invalidReviewRequest.getStar())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage/reviews"))
			.andExpect(flash().attributeExists("globalErrorMessage"));

		verify(reviewService, never()).addReview(anyLong(), anyLong(), any(ReviewRequest.class));
	}

	@Test
	@DisplayName("리뷰 수정 성공")
	void updateReview_Success() throws Exception {
		Long reviewId = 1L;
		ReviewRequest reviewRequest = new ReviewRequest(5, "수정된 리뷰 내용입니다. 아주 만족합니다.", Collections.emptyList());

		doNothing().when(reviewService).updateReivew(anyLong(), any(ReviewRequest.class));

		mockMvc.perform(post("/mypage/reviews/edit/{reviewId}", reviewId)
				.with(csrf())
				.with(user(testUserPrincipal))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("reviewContent", reviewRequest.getReviewContent())
				.param("star", String.valueOf(reviewRequest.getStar())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage/reviews"))
			.andExpect(flash().attributeExists("globalSuccessMessage"));

		verify(reviewService, times(1)).updateReivew(
			eq(reviewId),
			eq(reviewRequest)
		);
	}

	@Test
	@DisplayName("리뷰 수정 실패 - 유효성 검사 오류")
	void updateReview_Failure_Validation() throws Exception {
		Long reviewId = 1L;
		ReviewRequest invalidReviewRequest = new ReviewRequest(0, "짧아요", Collections.emptyList());

		mockMvc.perform(post("/mypage/reviews/edit/{reviewId}", reviewId)
				.with(csrf())
				.with(user(testUserPrincipal))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("reviewContent", invalidReviewRequest.getReviewContent())
				.param("star", String.valueOf(invalidReviewRequest.getStar())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage/reviews"))
			.andExpect(flash().attributeExists("globalErrorMessage"));

		verify(reviewService, never()).updateReivew(anyLong(), any(ReviewRequest.class));
	}

	@Test
	@DisplayName("리뷰 수정 실패 - 서비스 로직 오류")
	void updateReview_Failure_ServiceError() throws Exception {
		Long reviewId = 1L;
		ReviewRequest reviewRequest = new ReviewRequest(5, "수정된 리뷰 내용입니다. 아주 만족합니다.", Collections.emptyList());

		doThrow(new RuntimeException("리뷰 수정 중 예상치 못한 오류 발생")).when(reviewService)
			.updateReivew(anyLong(), any(ReviewRequest.class));

		mockMvc.perform(post("/mypage/reviews/edit/{reviewId}", reviewId)
				.with(csrf())
				.with(user(testUserPrincipal))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("reviewContent", reviewRequest.getReviewContent())
				.param("star", String.valueOf(reviewRequest.getStar())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage/reviews"))
			.andExpect(flash().attributeExists("globalErrorMessage"));

		verify(reviewService, times(1)).updateReivew(
			eq(reviewId),
			eq(reviewRequest)
		);
	}
}