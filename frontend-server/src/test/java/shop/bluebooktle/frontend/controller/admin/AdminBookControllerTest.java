package shop.bluebooktle.frontend.controller.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.request.BookFormRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.AdminAuthorService;
import shop.bluebooktle.frontend.service.AdminBookService;
import shop.bluebooktle.frontend.service.AdminCategoryService;
import shop.bluebooktle.frontend.service.AdminImgService;
import shop.bluebooktle.frontend.service.AdminPublisherService;
import shop.bluebooktle.frontend.service.AdminTagService;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;

@WebMvcTest(
	controllers = AdminBookController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = GlobalUserInfoAdvice.class
	)
)
@ActiveProfiles("test")
public class AdminBookControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminBookService adminBookService;
	@MockitoBean
	private AdminImgService adminImgService;
	@MockitoBean
	private AdminAuthorService adminAuthorService;
	@MockitoBean
	private AdminPublisherService adminPublisherService;
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
	@DisplayName("도서 목록 조회 - 검색 키워드 없음")
	void listBooks_withoutSearchKeyword() throws Exception {
		AdminBookResponse book = AdminBookResponse.builder()
			.bookId(1L)
			.isbn("1234567890")
			.title("도서 제목")
			.authors(List.of("작가1"))
			.publishers(List.of("출판사1"))
			.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
			.salePrice(BigDecimal.valueOf(9000))
			.stock(10)
			.publishDate(LocalDateTime.now())
			.build();

		Page<AdminBookResponse> bookPage = new PageImpl<>(List.of(book));
		Page<CategoryResponse> categoryPage = new PageImpl<>(List.of(
			new CategoryResponse(1L, "카테고리1", "-", "/1")
		));

		when(adminBookService.getPagedBooksByAdmin(0, 10, null)).thenReturn(bookPage);
		when(adminCategoryService.getCategories(anyInt(), anyInt(), any())).thenReturn(categoryPage);

		mockMvc.perform(get("/admin/books")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/book_list"))
			.andExpect(model().attributeExists("books"))
			.andExpect(model().attribute("currentPageZeroBased", 0))
			.andExpect(model().attribute("totalElements", 1L));
	}

	@Test
	@DisplayName("도서 목록 조회 - 검색 키워드 포함")
	void listBooks_withSearchKeyword() throws Exception {
		AdminBookResponse book = AdminBookResponse.builder()
			.bookId(2L)
			.isbn("9876543210")
			.title("검색 테스트 도서")
			.authors(List.of("작가2"))
			.publishers(List.of("출판사2"))
			.bookSaleInfoState(BookSaleInfoState.LOW_STOCK)
			.salePrice(BigDecimal.valueOf(8000))
			.stock(5)
			.publishDate(LocalDateTime.now())
			.build();

		Page<AdminBookResponse> bookPage = new PageImpl<>(List.of(book));
		Page<CategoryResponse> categoryPage = new PageImpl<>(List.of(
			new CategoryResponse(1L, "카테고리1", "-", "/1")
		));

		when(adminBookService.getPagedBooksByAdmin(0, 10, "테스트")).thenReturn(bookPage);
		when(adminCategoryService.getCategories(anyInt(), anyInt(), any())).thenReturn(categoryPage);

		mockMvc.perform(get("/admin/books")
				.param("page", "0")
				.param("size", "10")
				.param("searchKeyword", "테스트"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/book_list"))
			.andExpect(model().attributeExists("books"))
			.andExpect(model().attribute("searchKeyword", "테스트"));
	}

	@Test
	@DisplayName("도서 등록 폼 페이지 접근 - bookForm 초기화 확인")
	void bookForm_shouldRenderForm() throws Exception {
		when(adminAuthorService.getAuthors(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));
		when(adminPublisherService.getPublishers(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));
		when(adminTagService.getTags(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));
		when(adminCategoryService.getCategoryTree()).thenReturn(List.of());

		mockMvc.perform(get("/admin/books/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/book_form"))
			.andExpect(model().attributeExists("bookForm"));
	}

	@Test
	@DisplayName("도서 등록 폼 - bookForm이 없는 경우 초기값으로 설정된다")
	void bookForm_withoutBookForm_setsDefault() throws Exception {
		when(adminAuthorService.getAuthors(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminPublisherService.getPublishers(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminTagService.getTags(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminCategoryService.getCategoryTree())
			.thenReturn(List.of());

		mockMvc.perform(get("/admin/books/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/book_form"))
			.andExpect(model().attributeExists("bookForm"))
			.andExpect(model().attribute("pageTitle", "새 도서 등록"))
			.andExpect(model().attributeExists("stateOptions"));
	}

	@Test
	@DisplayName("도서 수정 폼 - 성공적으로 로딩")
	void bookEditForm_success() throws Exception {
		BookAllResponse mockBook = BookAllResponse.builder().imgUrl("https://image.aladin.co.kr/sample.jpg").build();
		when(adminBookService.getBook(1L)).thenReturn(mockBook);
		when(adminAuthorService.getAuthors(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));
		when(adminPublisherService.getPublishers(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));
		when(adminTagService.getTags(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));
		when(adminCategoryService.getCategoryTree()).thenReturn(List.of());

		mockMvc.perform(get("/admin/books/1/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/book_edit_form"))
			.andExpect(model().attributeExists("bookForm"));
	}

	@Test
	@DisplayName("도서 등록 폼 - 최초 접근 시 bookForm 초기값 설정")
	void bookForm_whenNoBookFormAttribute_setsDefaultBookForm() throws Exception {
		when(adminAuthorService.getAuthors(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminPublisherService.getPublishers(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminTagService.getTags(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminCategoryService.getCategoryTree()).thenReturn(List.of());

		mockMvc.perform(get("/admin/books/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/book_form"))
			.andExpect(model().attributeExists("bookForm"))
			.andExpect(model().attribute("pageTitle", "새 도서 등록"))
			.andExpect(model().attribute("stateOptions", List.of(
				BookSaleInfoState.AVAILABLE.name(),
				BookSaleInfoState.LOW_STOCK.name(),
				BookSaleInfoState.SALE_ENDED.name(),
				BookSaleInfoState.DELETED.name()
			)));
	}

	@Test
	@DisplayName("도서 등록 폼 - bookForm 속성이 이미 존재할 경우 초기값 생성 로직은 타지 않는다")
	void bookForm_withBookFormAttribute_doesNotOverride() throws Exception {
		BookFormRequest existingForm = new BookFormRequest();
		existingForm.setTitle("기존 값");

		when(adminAuthorService.getAuthors(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminPublisherService.getPublishers(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminTagService.getTags(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminCategoryService.getCategoryTree()).thenReturn(List.of());

		mockMvc.perform(get("/admin/books/new")
				.flashAttr("bookForm", existingForm)) // 이미 존재하는 상태로 전달
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/book_form"))
			.andExpect(model().attributeExists("bookForm"))
			.andExpect(model().attribute("bookForm", existingForm)) // 기존 값 유지되는지 확인
			.andExpect(model().attribute("pageTitle", "새 도서 등록"))
			.andExpect(model().attributeExists("stateOptions"));
	}

	@Test
	@DisplayName("도서 수정 폼 - 알라딘 이미지가 아닌 경우")
	void bookEditForm_notAladinImage() throws Exception {
		BookAllResponse mockBook = BookAllResponse.builder()
			.imgUrl("https://cdn.myserver.com/book_cover.jpg") // "aladin"이 없는 URL
			.title("테스트 도서")
			.isbn("1234567890123")
			.index("목차")
			.description("설명")
			.publishDate(LocalDateTime.now())
			.price(BigDecimal.valueOf(10000))
			.salePrice(BigDecimal.valueOf(9000))
			.stock(10)
			.isPackable(true)
			.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
			.authors(List.of())
			.publishers(List.of())
			.categories(List.of())
			.tags(List.of())
			.viewCount(0L)
			.searchCount(0L)
			.reviewCount(0L)
			.star(BigDecimal.ZERO)
			.createdAt(LocalDateTime.now())
			.build();

		when(adminBookService.getBook(1L)).thenReturn(mockBook);
		when(adminAuthorService.getAuthors(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));
		when(adminPublisherService.getPublishers(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));
		when(adminTagService.getTags(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of()));
		when(adminCategoryService.getCategoryTree()).thenReturn(List.of());

		mockMvc.perform(get("/admin/books/1/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/book_edit_form"))
			.andExpect(model().attribute("isAladinImg", false)) // 핵심 검증 포인트
			.andExpect(model().attributeExists("bookForm"));
	}

	@Test
	@DisplayName("도서 등록 - 유효성 검사 실패 시 리디렉션")
	void bookEditForm_fail_redirect() throws Exception {
		when(adminAuthorService.getAuthors(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminPublisherService.getPublishers(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminTagService.getTags(anyInt(), anyInt(), any()))
			.thenReturn(new PageImpl<>(List.of()));
		when(adminCategoryService.getCategoryTree())
			.thenReturn(List.of());

		when(adminBookService.getBook(1L)).thenThrow(new RuntimeException("error"));

		mockMvc.perform(get("/admin/books/1/edit"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"));
	}

	@Test
	@DisplayName("도서 수정 - 유효성 검사 실패 시 리디렉션 처리")
	void saveBook_validationFail() throws Exception {
		mockMvc.perform(post("/admin/books/save")
				.param("title", "") // 필수 값 누락
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books/new"))
			.andExpect(flash().attributeExists("globalErrorMessage"));
	}

	@Test
	@DisplayName("도서 등록 - 성공적으로 등록")
	void saveBook_success() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"imageFile",
			"cover.jpg",
			"image/jpeg",
			"test image content".getBytes()
		);

		mockMvc.perform(multipart("/admin/books/save")
				.file(image)
				.param("title", "테스트 도서")
				.param("isbn", "1234567890123") // 13자 필수
				.param("description", "도서 설명입니다.")
				.param("index", "도서 목차입니다.")
				.param("publishDate", "2024-01-01")
				.param("price", "10000")
				.param("salePrice", "9000")
				.param("stock", "10")
				.param("isPackable", "true")
				.param("state", "AVAILABLE")
				.param("authorIdList", "1")
				.param("publisherIdList", "1")
				.param("categoryIdList", "1")
				.param("tagIdList", "1")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"))
			.andExpect(flash().attributeExists("globalSuccessMessage"));
	}

	@Test
	@DisplayName("도서 등록 - 예외 발생 시 리디렉션")
	void saveBook_exception() throws Exception {
		doThrow(new RuntimeException("DB 오류")).when(adminBookService).registerBook(any());

		mockMvc.perform(post("/admin/books/save")
				.param("title", "테스트")
				.param("isbn", "1234567890")
				.param("index", "목차")
				.param("description", "설명")
				.param("publishDate", "2024-01-01")
				.param("price", "10000")
				.param("salePrice", "9000")
				.param("stock", "10")
				.param("state", "AVAILABLE")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books/new"))
			.andExpect(flash().attributeExists("globalErrorMessage"));
	}

	@Test
	@DisplayName("도서 등록 - 서비스 예외 발생 시 실패 처리")
	void saveBook_serviceException() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"imageFile", "cover.jpg", "image/jpeg", "image".getBytes()
		);

		doThrow(new RuntimeException("DB 오류")).when(adminBookService).registerBook(any());

		mockMvc.perform(multipart("/admin/books/save")
				.file(image)
				.param("title", "테스트 도서")
				.param("isbn", "1234567890123")
				.param("description", "설명입니다.")
				.param("index", "목차입니다.")
				.param("publishDate", "2024-01-01")
				.param("price", "10000")
				.param("salePrice", "9000")
				.param("stock", "10")
				.param("isPackable", "true")
				.param("state", "AVAILABLE")
				.param("authorIdList", "1")
				.param("publisherIdList", "1")
				.param("categoryIdList", "1")
				.param("tagIdList", "1")
			)
			.andExpect(status().is3xxRedirection()) // 리디렉션 확인
			.andExpect(redirectedUrl("/admin/books/new")) // 예상 리디렉션 경로
			.andExpect(flash().attributeExists("globalErrorMessage")) // 에러 메시지 전달 확인
			.andExpect(flash().attributeExists("bookForm")); // 입력값 보존 확인
	}


	@Test
	@DisplayName("도서 수정 - 유효성 검사 실패 시 리디렉션")
	void updateBook_validationFail() throws Exception {
		mockMvc.perform(post("/admin/books/update/1")
				.param("title", ""))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"))
			.andExpect(flash().attributeExists("globalErrorMessage"));
	}

	@Test
	@DisplayName("도서 수정 - 성공적으로 수정")
	void updateBook_success() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"imageFile", "cover.jpg", "image/jpeg", "test".getBytes()
		);

		mockMvc.perform(
				multipart("/admin/books/update/1")
					.file(image)
					.param("title", "수정도서")
					.param("description", "수정된 설명입니다.")
					.param("index", "수정된 목차입니다.")
					.param("publishDate", "2024-01-01")
					.param("price", "15000")
					.param("salePrice", "13000")
					.param("stock", "20")
					.param("isPackable", "true")
					.param("state", "AVAILABLE")
					.param("authorIdList", "1")
					.param("publisherIdList", "1")
					.param("categoryIdList", "1")
					.param("tagIdList", "1")
					.param("isAladinImg", "false")
					.with((RequestPostProcessor) request -> {
						request.setMethod("POST");
						return request;
					})
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"))
			.andExpect(flash().attributeExists("globalSuccessMessage"));
	}
	@Test
	@DisplayName("도서 수정 - 예외 발생 시 실패 처리")
	void updateBook_exception() throws Exception {
		doThrow(new RuntimeException("DB 오류")).when(adminBookService).updateBook(eq(1L), any());

		mockMvc.perform(post("/admin/books/update/1")
				.param("title", "수정도서")
				.param("state", "AVAILABLE"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"))
			.andExpect(flash().attributeExists("globalErrorMessage"));
	}

	@Test
	@DisplayName("도서 수정 - 서비스 예외 발생 시 실패 처리")
	void updateBook_serviceException() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"imageFile", "updated.jpg", "image/jpeg", "image".getBytes()
		);

		doThrow(new RuntimeException("DB 오류"))
			.when(adminBookService).updateBook(eq(1L), any());

		mockMvc.perform(multipart("/admin/books/update/1")
				.file(image)
				.param("title", "수정도서")
				.param("description", "설명입니다.")
				.param("index", "목차입니다.")
				.param("publishDate", "2024-01-01")
				.param("price", "15000")
				.param("salePrice", "13000")
				.param("stock", "20")
				.param("isPackable", "true")
				.param("state", "AVAILABLE")
				.param("authorIdList", "1")
				.param("publisherIdList", "1")
				.param("categoryIdList", "1")
				.param("tagIdList", "1")
				.param("isAladinImg", "false")
				.with(request -> {
					request.setMethod("POST"); // POST로 강제
					return request;
				})
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"))
			.andExpect(flash().attributeExists("globalErrorMessage"));
	}

	@Test
	@DisplayName("도서 삭제 - 성공적으로 삭제")
	void deleteBook_success() throws Exception {
		doNothing().when(adminBookService).deleteBook(1L);

		mockMvc.perform(post("/admin/books/1/delete"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"))
			.andExpect(flash().attributeExists("globalSuccessMessage"));
	}
}
