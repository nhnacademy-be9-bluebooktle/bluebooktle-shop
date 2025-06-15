package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.response.*;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.*;
import shop.bluebooktle.frontend.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
	@Mock private AdminBookRepository adminBookRepository;
	@Mock private BookRepository bookRepository;
	@Mock private BookCategoryRepository bookCategoryRepository;
	@Mock private CategoryRepository categoryRepository;

	@InjectMocks
	private BookServiceImpl bookService;

	private BookInfoResponse book;
	private PaginationData<BookInfoResponse> paginationData;
	private BookDetailResponse bookDetailResponse;

	@BeforeEach
	void setUp() {
		book = new BookInfoResponse(
			1L,
			"제목",
			List.of("저자"),
			BigDecimal.valueOf(9000),
			BigDecimal.valueOf(10000),
			"/images",
			LocalDateTime.now(),
			BigDecimal.valueOf(4.5),
			1L,
			1L
		);
		List<BookInfoResponse> books = List.of(book);
		paginationData = new PaginationData<>(
			books,
			new PaginationData.PaginationInfo(
				1, 1L, 0, 10, true, true, false, false
			)
		);

		bookDetailResponse = new BookDetailResponse(
			"9781234567890",
			"자바의 정석",
			List.of("남궁성"),
			List.of("도우출판"),
			BigDecimal.valueOf(30000),
			BigDecimal.valueOf(27000),
			10,
			"자바 프로그래밍 입문서입니다.",
			"1장: 변수, 2장: 연산자, ...",
			"/images/java.jpg",
			BookSaleInfoState.AVAILABLE
		);
	}

	@Test
	@DisplayName("장바구니 도서 정보 조회")
	void getBookCartOrder_success() {
		Long bookId = 1L;
		int quantity = 2;
		BookCartOrderResponse expected = new BookCartOrderResponse(
			bookId,
			"책 제목",
			BigDecimal.valueOf(10000),
			BigDecimal.valueOf(9000),
			"/images",
			List.of("카테고리"),
			true,
			quantity
		);
		when(adminBookRepository.getBookCartOrder(bookId, quantity)).thenReturn(expected);

		BookCartOrderResponse result = bookService.getBookCartOrder(bookId, quantity);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	@DisplayName("도서 검색 페이지 조회")
	void getPagedBooks_success() {
		when(bookRepository.searchBooks(0, 10, "테스트", BookSortType.POPULARITY)).thenReturn(paginationData);

		Page<BookInfoResponse> result = bookService.getPagedBooks(0, 10, "테스트", BookSortType.POPULARITY);

		assertThat(result.getContent()).containsExactly(book);
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("카테고리 기반 도서 페이지 조회")
	void getPagedBooksByCategoryId_success() {
		Long categoryId = 1L;
		int page = 0, size = 5;
		when(bookCategoryRepository.getBooksByCategory(page, size, BookSortType.NEWEST, categoryId)).thenReturn(paginationData);

		Page<BookInfoResponse> result = bookService.getPagedBooksByCategoryId(page, size, BookSortType.NEWEST, categoryId);

		assertThat(result.getContent()).contains(book);
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("카테고리 정보 조회")
	void getCategoryById_success() {
		Long categoryId = 10L;
		CategoryResponse category = new CategoryResponse(categoryId, "카테고리", "-", "/1");
		when(categoryRepository.getCategory(categoryId)).thenReturn(category);

		CategoryResponse result = bookService.getCategoryById(categoryId);

		assertThat(result).isEqualTo(category);
	}

	@Test
	@DisplayName("도서 상세 조회")
	void getBookById_success() {
		Long bookId = 1L;
		when(bookRepository.getBookDetail(bookId)).thenReturn(bookDetailResponse);

		BookDetailResponse result = bookService.getBookDetail(bookId);

		assertThat(result).isEqualTo(bookDetailResponse);
	}

	@Test
	@DisplayName("도서 찜 등록")
	void likeBook_success() {
		Long bookId = 1L;
		doNothing().when(bookRepository).likeBook(bookId);

		bookService.like(bookId);

		verify(bookRepository).likeBook(bookId);
	}

	@Test
	@DisplayName("도서 찜 취소")
	void unlikeBook_success() {
		Long bookId = 1L;
		doNothing().when(bookRepository).unlikeBook(bookId);

		bookService.unlike(bookId);

		verify(bookRepository).unlikeBook(bookId);
	}

	@Test
	@DisplayName("도서 찜 여부 확인")
	void isLiked_success() {
		Long bookId = 1L;
		BookLikesResponse response = new BookLikesResponse(bookId, true, 100);
		when(bookRepository.isLiked(bookId)).thenReturn(response);

		boolean result = bookService.isLiked(bookId);

		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("도서 찜 개수 확인")
	void countLikes_success() {
		Long bookId = 1L;
		BookLikesResponse response = new BookLikesResponse(bookId, true, 100);
		when(bookRepository.countLikes(bookId)).thenReturn(response);

		int result = bookService.countLikes(bookId);

		assertThat(result).isEqualTo(100);
	}
}
