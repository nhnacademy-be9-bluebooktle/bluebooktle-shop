package shop.bluebooktle.frontend.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.common.dto.review.response.ReviewResponse;
import shop.bluebooktle.frontend.service.AdminPointService;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;
import shop.bluebooktle.frontend.service.DeliveryRuleService;
import shop.bluebooktle.frontend.service.ReviewService;

@WebMvcTest(BookController.class)
@ActiveProfiles("test")
public class BookControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	BookService bookService;
	@MockitoBean
	DeliveryRuleService deliveryRuleService;
	@MockitoBean
	ReviewService reviewService;
	@MockitoBean
	AdminPointService adminPointService;
	@MockitoBean
	CartService cartService;
	@MockitoBean
	CategoryService categoryService;

	@Test
	@DisplayName("도서 목록 페이지 조회 성공")
	void getBookListPage_success() throws Exception {
		// given
		Page<BookInfoResponse> dummyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
		given(bookService.getPagedBooks(anyInt(), anyInt(), any(), any())).willReturn(dummyPage);

		// when & then
		mockMvc.perform(get("/books"))
			.andExpect(status().isOk())
			.andExpect(view().name("book/book_list"))
			.andExpect(model().attributeExists("pagedBooks", "sortTypes", "bookSortType", "size", "filterCount"));
	}

	@Test
	@DisplayName("카테고리별 도서 목록 페이지 조회 성공")
	void getBooksByCategoryPage_success() throws Exception {
		// given
		long categoryId = 1L;
		Page<BookInfoResponse> dummyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
		CategoryResponse dummyCategory = new CategoryResponse(categoryId, "카테고리", null, "/1");

		given(bookService.getPagedBooksByCategoryId(anyInt(), anyInt(), any(), eq(categoryId))).willReturn(dummyPage);
		given(bookService.getCategoryById(categoryId)).willReturn(dummyCategory);

		// when & then
		mockMvc.perform(get("/categories/{categoryId}", categoryId))
			.andExpect(status().isOk())
			.andExpect(view().name("book/book_list"))
			.andExpect(model().attributeExists("pagedBooks", "sortTypes", "bookSortType", "category", "size", "filterCount"));
	}


	@Test
	@DisplayName("도서 찜 등록 성공")
	void likeBook_success() throws Exception {
		// given
		long bookId = 1L;
		willDoNothing().given(bookService).like(bookId);

		// when & then
		mockMvc.perform(get("/books/{bookId}/likes", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId))
			.andExpect(flash().attribute("globalSuccessMessage", "찜 완료!"));

		then(bookService).should().like(bookId);
	}

	@Test
	@DisplayName("도서 찜 등록 실패")
	void likeBook_failure() throws Exception {
		long bookId = 1L;
		willThrow(new RuntimeException("fail")).given(bookService).like(bookId);

		mockMvc.perform(get("/books/{bookId}/likes", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId))
			.andExpect(flash().attributeExists("globalErrorMessage"));

		then(bookService).should().like(bookId);
	}

	@Test
	@DisplayName("도서 찜 취소 성공")
	void unlikeBook_success() throws Exception {
		long bookId = 1L;
		willDoNothing().given(bookService).unlike(bookId);

		mockMvc.perform(post("/books/{bookId}/unlikes", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId))
			.andExpect(flash().attribute("globalSuccessMessage", "찜 취소!"));

		then(bookService).should().unlike(bookId);
	}

	@Test
	@DisplayName("도서 찜 취소 실패")
	void unlikeBook_failure() throws Exception {
		long bookId = 1L;
		willThrow(new RuntimeException("fail")).given(bookService).unlike(bookId);

		mockMvc.perform(post("/books/{bookId}/unlikes", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId))
			.andExpect(flash().attributeExists("globalErrorMessage"));

		then(bookService).should().unlike(bookId);
	}


	@Test
	@DisplayName("리뷰 좋아요 등록 성공")
	void toggleReviewLike_likedTrue() throws Exception {
		// given
		long reviewId = 1L;
		long bookId = 10L;
		given(reviewService.toggleReviewLike(reviewId)).willReturn(true);

		// when & then
		mockMvc.perform(post("/reviews/{reviewId}/like", reviewId)
				.param("bookId", String.valueOf(bookId)))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId + "?fragment=reviews-content"))
			.andExpect(flash().attribute("globalSuccessMessage", "리뷰 좋아요!"));

		then(reviewService).should().toggleReviewLike(reviewId);
	}

	@Test
	@DisplayName("리뷰 좋아요 취소 성공")
	void toggleReviewLike_likedFalse() throws Exception {
		// given
		long reviewId = 1L;
		long bookId = 10L;
		given(reviewService.toggleReviewLike(reviewId)).willReturn(false);

		// when & then
		mockMvc.perform(post("/reviews/{reviewId}/like", reviewId)
				.param("bookId", String.valueOf(bookId)))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId + "?fragment=reviews-content"))
			.andExpect(flash().attribute("globalSuccessMessage", "리뷰 좋아요 취소!"));

		then(reviewService).should().toggleReviewLike(reviewId);
	}

	@Test
	@DisplayName("리뷰 좋아요 처리 실패")
	void toggleReviewLike_failure() throws Exception {
		// given
		long reviewId = 1L;
		long bookId = 10L;
		given(reviewService.toggleReviewLike(reviewId)).willThrow(new RuntimeException("DB 에러"));

		// when & then
		mockMvc.perform(post("/reviews/{reviewId}/like", reviewId)
				.param("bookId", String.valueOf(bookId)))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/books/" + bookId + "?fragment=reviews-content"))
			.andExpect(flash().attributeExists("globalErrorMessage"));

		then(reviewService).should().toggleReviewLike(reviewId);
	}
}
