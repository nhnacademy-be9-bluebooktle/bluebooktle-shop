package shop.bluebooktle.frontend.controller.myPage;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.controller.myPage.MyPageBookLikesController;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;
import shop.bluebooktle.frontend.service.MyPageBookLikesService;

@WebMvcTest(MyPageBookLikesController.class)
@ActiveProfiles("test")
public class MyPageBookLikesControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MyPageBookLikesService myPageBookLikesService;
	@MockitoBean
	private CartService cartService;
	@MockitoBean
	private CategoryService categoryService;

	@Test
	@DisplayName("도서 목록 조회 - 성공")
	void listBookLikes_success() throws Exception {
		// given
		List<BookLikesListResponse> content = List.of(
			BookLikesListResponse.builder()
				.bookId(1L)
				.bookName("테스트 도서")
				.authorName(List.of("홍길동"))
				.imgUrl("https://dummy.com/1.png")
				.price(BigDecimal.valueOf(12000))
				.build()
		);

		PaginationData.PaginationInfo pageInfo = new PaginationData.PaginationInfo(1, 1, 0, 10, true, true, false,
			false);

		PaginationData<BookLikesListResponse> page =
			new PaginationData<>(content, pageInfo);

		given(myPageBookLikesService.getMyPageBookLikes(0, 10)).willReturn(page);

		// when ‒ then
		mockMvc.perform(get("/mypage/likes").param("page", "0").param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(view().name("mypage/likes_list"))
			.andExpect(model().attribute("likes", content))
			.andExpect(model().attribute("pagination", pageInfo));
	}

	@Test
	@DisplayName("도서 좋아요 삭제 - 성공")
	void deleteBookLikes_success() throws Exception {
		long bookId = 1L;
		willDoNothing().given(myPageBookLikesService).unlike(bookId);

		mockMvc.perform(post("/mypage/likes/{bookId}/delete", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage/likes"))
			.andExpect(flash().attribute("globalSuccessMessage",
				"찜이 성공적으로 해제되었습니다."));

		then(myPageBookLikesService).should().unlike(bookId);
	}

	@Test
	@DisplayName("도서 좋아요 삭제 - 실패")
	void deleteBookLikesFailure() throws Exception {
		long bookId = 1L;
		willThrow(new RuntimeException("DB 오류"))
			.given(myPageBookLikesService).unlike(bookId);

		mockMvc.perform(post("/mypage/likes/{bookId}/delete", bookId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage/likes"))
			.andExpect(flash().attribute("globalErrorMessage",
				Matchers.startsWith("찜 해제에 실패했습니다")));

		then(myPageBookLikesService).should().unlike(bookId);
	}
}
