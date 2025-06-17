package shop.bluebooktle.frontend.controller.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.AdminBookService;
import shop.bluebooktle.frontend.service.AdminCategoryService;
import shop.bluebooktle.frontend.service.AdminTagService;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;

@WebMvcTest(
	controllers = AdminAladinBookController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = GlobalUserInfoAdvice.class
	)
)
@ActiveProfiles("test")
class AdminAladinBookControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminBookService adminBookService;
	@MockitoBean
	private AdminCategoryService adminCategoryService;
	@MockitoBean
	private AdminTagService adminTagService;
	@MockitoBean
	private BookService bookService;

	@MockitoBean
	private CartService cartService;

	@MockitoBean
	CategoryService categoryService;

	@Test
	@DisplayName("알라딘 도서 등록 폼 페이지 접근")
	void bookForm_success() throws Exception {
		when(adminCategoryService.getCategoryTree()).thenReturn(List.of());
		when(adminTagService.getTags(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));

		mockMvc.perform(get("/admin/aladin/books/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/aladin_book/aladin_book_form"))
			.andExpect(model().attributeExists("aladinBookForm"))
			.andExpect(model().attributeExists("allCategoriesForMapping"))
			.andExpect(model().attributeExists("allTagsForMapping"))
			.andExpect(model().attribute("pageTitle", "\uC54C\uB77C\uB518 API \uB3C4\uC11C \uB4F1\uB85D"));
	}

	@Test
	@DisplayName("알라딘 도서 등록 폼 - aladinBookForm 속성이 이미 존재할 경우 초기값 설정 로직은 실행되지 않는다")
	void bookForm_withExistingAladinBookForm_doesNotOverride() throws Exception {
		BookAllRegisterByAladinRequest existingForm =
			new BookAllRegisterByAladinRequest("기존 제목", "1234567890123", 10, true, BookSaleInfoState.AVAILABLE,
				List.of(1L), List.of(1L));

		when(adminCategoryService.getCategoryTree()).thenReturn(List.of());
		when(adminTagService.getTags(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));

		mockMvc.perform(get("/admin/aladin/books/new")
				.flashAttr("aladinBookForm", existingForm)) // 이미 존재하는 경우
			.andExpect(status().isOk())
			.andExpect(view().name("admin/aladin_book/aladin_book_form"))
			.andExpect(model().attribute("aladinBookForm", existingForm)) // 기존 값 유지 확인
			.andExpect(model().attribute("pageTitle", "알라딘 API 도서 등록"))
			.andExpect(model().attributeExists("allCategoriesForMapping"))
			.andExpect(model().attributeExists("allTagsForMapping"))
			.andExpect(model().attribute("stateOptions", Arrays.asList(
				BookSaleInfoState.AVAILABLE.name(),
				BookSaleInfoState.LOW_STOCK.name(),
				BookSaleInfoState.SALE_ENDED.name(),
				BookSaleInfoState.DELETED.name()
			)));
	}

	@Test
	@DisplayName("알라딘 도서 저장 성공")
	void saveBook_success() throws Exception {
		mockMvc.perform(post("/admin/aladin/books/save")
				.param("title", "테스트")
				.param("isbn", "1234567890123")
				.param("stock", "10")
				.param("isPackable", "true")
				.param("state", "AVAILABLE")
				.param("categoryIdList", "1")
				.param("tagIdList", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"))
			.andExpect(flash().attributeExists("globalSuccessMessage"));
	}

	@Test
	@DisplayName("알라딘 도서 저장 실패 - 바인딩 에러")
	void saveBook_bindingError() throws Exception {
		mockMvc.perform(post("/admin/aladin/books/save")
				.param("title", "") // title 누락
				.param("isbn", "") // isbn 누락
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/aladin/books/new"))
			.andExpect(flash().attributeExists("globalErrorMessage"));
	}

	@Test
	@DisplayName("알라딘 도서 저장 실패 - 서비스 예외")
	void saveBook_serviceException() throws Exception {
		doThrow(new RuntimeException("DB 오류")).when(adminBookService).registerBookByAladin(any());

		mockMvc.perform(post("/admin/aladin/books/save")
				.param("title", "테스트")
				.param("isbn", "1234567890123")
				.param("stock", "10")
				.param("isPackable", "true")
				.param("state", "AVAILABLE")
				.param("categoryIdList", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/aladin/books/new"))
			.andExpect(flash().attributeExists("globalErrorMessage"));
	}

	@Test
	@DisplayName("알라딘 API 검색 응답 성공")
	void aladinSearch_success() throws Exception {
		List<AladinBookResponse> mockList = List.of(new AladinBookResponse());
		when(adminBookService.searchAladin("검색어", 1, 10)).thenReturn(mockList);

		mockMvc.perform(get("/admin/aladin/books/aladin-search")
				.param("keyword", "검색어"))
			.andExpect(status().isOk());
	}
}
