package shop.bluebooktle.frontend.controller.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.AdminAuthorService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;

@WebMvcTest(
	controllers = AdminAuthorController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = GlobalUserInfoAdvice.class
	)
)
@ActiveProfiles("test")
class AdminAuthorControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminAuthorService adminAuthorService;

	@MockitoBean
	private CartService cartService;

	@MockitoBean
	CategoryService categoryService;

	@Test
	@DisplayName("작가 목록 페이지 뷰 반환")
	void listAuthorsView() throws Exception {
		AuthorResponse author1 = AuthorResponse.builder().id(1L).name("Author1").build();
		Page<AuthorResponse> page = new PageImpl<>(List.of(author1), PageRequest.of(0, 10), 1);
		when(adminAuthorService.getAuthors(0, 10, null)).thenReturn(page);

		mockMvc.perform(get("/admin/authors")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/author/author_list"))
			.andExpect(model().attributeExists("authors"))
			.andExpect(model().attribute("currentPage", 0));

		verify(adminAuthorService).getAuthors(0, 10, null);
	}

	@Test
	@DisplayName("작가 JSON 목록 조회")
	void listAuthorsJson() throws Exception {
		AuthorResponse author1 = AuthorResponse.builder().id(1L).name("Author1").build();
		AuthorResponse author2 = AuthorResponse.builder().id(2L).name("Author2").build();
		Page<AuthorResponse> page = new PageImpl<>(List.of(author1, author2), PageRequest.of(0, 10), 2);
		when(adminAuthorService.getAuthors(0, 10, null)).thenReturn(page);

		mockMvc.perform(get("/admin/authors/search")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].id").value(1));

		verify(adminAuthorService).getAuthors(0, 10, null);
	}

	@Test
	@DisplayName("작가 목록 페이지 뷰 반환 - 검색 조건 포함")
	void listAuthorsViewWithSearchParams() throws Exception {
		AuthorResponse author1 = AuthorResponse.builder().id(1L).name("홍길동").build();
		Page<AuthorResponse> page = new PageImpl<>(List.of(author1), PageRequest.of(0, 10), 1);
		when(adminAuthorService.getAuthors(0, 10, "길동")).thenReturn(page);

		mockMvc.perform(get("/admin/authors")
				.param("page", "0")
				.param("size", "10")
				.param("searchField", "name") // searchField 존재
				.param("searchKeyword", "길동")) // searchKeyword 존재
			.andExpect(status().isOk())
			.andExpect(view().name("admin/author/author_list"))
			.andExpect(model().attributeExists("authors"))
			.andExpect(model().attribute("currentPage", 0))
			.andExpect(model().attribute("searchField", "name"))
			.andExpect(model().attribute("searchKeyword", "길동"));

		verify(adminAuthorService).getAuthors(0, 10, "길동");
	}

	@Test
	@DisplayName("작가 목록 페이지 뷰 반환 - searchKeyword는 빈 문자열")
	void listAuthorsViewWithEmptySearchKeyword() throws Exception {
		AuthorResponse author1 = AuthorResponse.builder().id(1L).name("홍길동").build();
		Page<AuthorResponse> page = new PageImpl<>(List.of(author1), PageRequest.of(0, 10), 1);
		when(adminAuthorService.getAuthors(0, 10, "")).thenReturn(page);

		mockMvc.perform(get("/admin/authors")
				.param("page", "0")
				.param("size", "10")
				.param("searchField", "name")
				.param("searchKeyword", "")) // empty string
			.andExpect(status().isOk())
			.andExpect(view().name("admin/author/author_list"))
			.andExpect(model().attributeExists("authors"))
			.andExpect(model().attribute("searchField", "name"))
			.andExpect(model().attribute("searchKeyword", ""));

		verify(adminAuthorService).getAuthors(0, 10, "");
	}

	@Test
	@DisplayName("작가 목록 페이지 뷰 반환 - searchField는 빈 문자열")
	void listAuthorsViewWithEmptySearchField() throws Exception {
		AuthorResponse author1 = AuthorResponse.builder().id(1L).name("김작가").build();
		Page<AuthorResponse> page = new PageImpl<>(List.of(author1), PageRequest.of(0, 10), 1);
		when(adminAuthorService.getAuthors(0, 10, "김")).thenReturn(page);

		mockMvc.perform(get("/admin/authors")
				.param("page", "0")
				.param("size", "10")
				.param("searchField", "") // 빈 문자열
				.param("searchKeyword", "김")) // 정상 검색어
			.andExpect(status().isOk())
			.andExpect(view().name("admin/author/author_list"))
			.andExpect(model().attributeExists("authors"))
			.andExpect(model().attribute("searchField", ""))
			.andExpect(model().attribute("searchKeyword", "김"));

		verify(adminAuthorService).getAuthors(0, 10, "김");
	}

	@Test
	@DisplayName("작가 삭제 후 목록 페이지로 리다이렉트")
	void deleteAuthorRedirect() throws Exception {
		doNothing().when(adminAuthorService).deleteAuthor(1L);

		mockMvc.perform(post("/admin/authors/1/delete"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/authors"));

		verify(adminAuthorService).deleteAuthor(1L);
	}

	@Test
	@DisplayName("작가 등록 폼 진입 (신규)")
	void authorFormNew() throws Exception {
		mockMvc.perform(get("/admin/authors/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/author/author_form"))
			.andExpect(model().attributeExists("author"))
			.andExpect(model().attribute("pageTitle", "새 작가 등록"));
	}

	@Test
	@DisplayName("작가 수정 폼 진입 (기존)")
	void authorFormEdit() throws Exception {
		AuthorResponse author = AuthorResponse.builder().id(1L).name("기존작가").build();
		when(adminAuthorService.getAuthor(1L)).thenReturn(author);

		mockMvc.perform(get("/admin/authors/1/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/author/author_form"))
			.andExpect(model().attribute("author", author))
			.andExpect(model().attribute("pageTitle", "작가 정보 수정 (ID: 1)"));

		verify(adminAuthorService).getAuthor(1L);
	}

	@Test
	@DisplayName("작가 등록 성공")
	void saveAuthorRegisterSuccess() throws Exception {
		doNothing().when(adminAuthorService)
			.addAuthor(any());

		mockMvc.perform(post("/admin/authors/save")
				.param("name", "새작가"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/authors"))
			.andExpect(flash().attribute("globalSuccessMessage", "작가 '새작가' 정보가 성공적으로 등록되었습니다."));

		verify(adminAuthorService).addAuthor(any());
	}

	@Test
	@DisplayName("작가 저장 실패 - 이름 없음 (수정 폼에서)")
	void saveAuthorValidationErrorWithId() throws Exception {
		mockMvc.perform(post("/admin/authors/save")
				.param("id", "5")
				.param("name", " ")) // 공백 입력
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/authors/5/edit"))
			.andExpect(flash().attributeExists("globalErrorMessage"))
			.andExpect(flash().attributeExists("author"))
			.andExpect(flash().attributeExists("org.springframework.validation.BindingResult.author"));
	}

	@Test
	@DisplayName("작가 저장 실패 - name이 null일 경우")
	void saveAuthorValidationErrorWithNullName() throws Exception {
		mockMvc.perform(post("/admin/authors/save"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/authors/new"))
			.andExpect(flash().attributeExists("globalErrorMessage"))
			.andExpect(flash().attributeExists("author"))
			.andExpect(flash().attributeExists("org.springframework.validation.BindingResult.author"));
	}

	@Test
	@DisplayName("작가 수정 성공")
	void saveAuthorUpdateSuccess() throws Exception {
		doNothing().when(adminAuthorService)
			.updateAuthor(eq(1L), any());

		mockMvc.perform(post("/admin/authors/save")
				.param("id", "1")
				.param("name", "수정작가"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/authors"))
			.andExpect(flash().attribute("globalSuccessMessage", "작가 '수정작가' 정보가 성공적으로 수정되었습니다."));

		verify(adminAuthorService).updateAuthor(eq(1L), any());
	}

	@Test
	@DisplayName("작가 저장 실패 - 이름 없음")
	void saveAuthorValidationError() throws Exception {
		mockMvc.perform(post("/admin/authors/save")
				.param("name", "")) // 빈 이름
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/authors/new"))
			.andExpect(flash().attributeExists("globalErrorMessage"))
			.andExpect(flash().attributeExists("author"));
	}

	@Test
	@DisplayName("작가 수정 중 예외 발생")
	void saveAuthorUpdateException() throws Exception {
		doThrow(new RuntimeException("DB 오류")).when(adminAuthorService)
			.updateAuthor(eq(1L), any());

		mockMvc.perform(post("/admin/authors/save")
				.param("id", "1")
				.param("name", "작가명"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/authors/1/edit"))
			.andExpect(flash().attributeExists("globalErrorMessage"))
			.andExpect(flash().attributeExists("author"));
	}

	@Test
	@DisplayName("작가 등록 중 예외 발생")
	void saveAuthorRegisterException() throws Exception {
		doThrow(new RuntimeException("DB 오류")).when(adminAuthorService)
			.addAuthor(any());

		mockMvc.perform(post("/admin/authors/save")
				.param("name", "새작가"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/authors/new"))
			.andExpect(flash().attributeExists("globalErrorMessage"))
			.andExpect(flash().attributeExists("author"));
	}

	@Test
	@DisplayName("작가 삭제 중 예외 발생 시 에러 메시지 출력 및 리다이렉트")
	void deleteAuthorException() throws Exception {
		doThrow(new RuntimeException("삭제 실패")).when(adminAuthorService).deleteAuthor(1L);

		mockMvc.perform(post("/admin/authors/1/delete"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/authors"))
			.andExpect(flash().attributeExists("globalErrorMessage"));

		verify(adminAuthorService).deleteAuthor(1L);
	}
}
