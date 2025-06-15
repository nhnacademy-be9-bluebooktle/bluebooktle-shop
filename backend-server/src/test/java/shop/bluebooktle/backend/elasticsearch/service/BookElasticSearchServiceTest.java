package shop.bluebooktle.backend.elasticsearch.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.elasticsearch.document.BookDocument;
import shop.bluebooktle.backend.elasticsearch.repository.BookElasticSearchCustomRepository;
import shop.bluebooktle.backend.elasticsearch.repository.BookElasticSearchRepository;
import shop.bluebooktle.backend.elasticsearch.service.impl.BookElasticSearchServiceImpl;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.elasticsearch.BookElasticSearchRegisterRequest;
import shop.bluebooktle.common.dto.elasticsearch.BookElasticSearchUpdateRequest;
import shop.bluebooktle.common.exception.elasticsearch.ElasticsearchBookDocumentSaveException;
import shop.bluebooktle.common.exception.elasticsearch.ElasticsearchBookNotFoundException;

public class BookElasticSearchServiceTest {

	@Mock
	private BookElasticSearchRepository repository;

	@Mock
	private BookElasticSearchCustomRepository customRepository;

	@InjectMocks
	private BookElasticSearchServiceImpl service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Nested
	@DisplayName("도서 등록")
	class RegisterBook {
		@Test
		@DisplayName("도서 등록 성공")
		void success() {
			BookElasticSearchRegisterRequest req = new BookElasticSearchRegisterRequest(
				1L, "title", "desc", LocalDateTime.now(), new BigDecimal("1000.00"), new BigDecimal("4.5"),
				10L, 5L, 3L, List.of("author"), List.of("pub"), List.of("tag"), List.of(1L)
			);
			assertThatCode(() -> service.registerBook(req)).doesNotThrowAnyException();
		}

		@Test
		@DisplayName("도서 등록 실패 시 예외 발생")
		void fail_throwsSaveException() {
			doThrow(new RuntimeException("error")).when(repository).save(any());
			BookElasticSearchRegisterRequest req = new BookElasticSearchRegisterRequest(
				1L, "title", "desc", LocalDateTime.now(), new BigDecimal("1000.00"), new BigDecimal("4.5"),
				10L, 5L, 3L, List.of("author"), List.of("pub"), List.of("tag"), List.of(1L)
			);
			assertThatThrownBy(() -> service.registerBook(req)).isInstanceOf(ElasticsearchBookDocumentSaveException.class);
		}
	}

	@Test
	@DisplayName("검색 수 증가")
	void updateSearchCount() {
		Book book = Book.builder().id(1L).build();
		BookDocument doc = BookDocument.builder().id(1L).searchCount(5L).build();
		when(repository.findByIdIn(List.of(1L))).thenReturn(List.of(doc));
		service.updateSearchCount(List.of(book));
		verify(repository).save(argThat(d -> d.getSearchCount() == 6));
	}

	@Test
	@DisplayName("조회 수 증가 - 성공")
	void updateViewCount_success() {
		Book book = Book.builder().id(1L).build();
		BookDocument doc = BookDocument.builder().id(1L).viewCount(3L).build();
		when(repository.findById(1L)).thenReturn(Optional.of(doc));
		service.updateViewCount(book);
		verify(repository).save(argThat(d -> d.getViewCount() == 4));
	}

	@Test
	@DisplayName("조회 수 증가 - 예외 발생(엘라스틱 존재 하지 않음)")
	void updateViewCount_notFound() {
		when(repository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.updateViewCount(Book.builder().id(1L).build()))
			.isInstanceOf(ElasticsearchBookNotFoundException.class);
	}

	@Test
	@DisplayName("리뷰 수, 평점 수정")
	void updateReviewCountAndStar() {
		Book book = Book.builder().id(1L).build();
		BookSaleInfo info = BookSaleInfo.builder().book(book).reviewCount(100L).star(BigDecimal.valueOf(4.7)).build();
		BookDocument doc = BookDocument.builder().id(1L).build();
		when(repository.findById(1L)).thenReturn(Optional.of(doc));
		service.updateReviewCountAndStar(info);
		verify(repository).save(argThat(d -> Objects.equals(d.getStar(), BigDecimal.valueOf(4.7))
			&& d.getReviewCount() == 100));
	}

	@Test
	@DisplayName("도서 정보 수정")
	void updateBook() {
		BookElasticSearchUpdateRequest req = new BookElasticSearchUpdateRequest(
			1L, "t", "d", LocalDateTime.now(), new BigDecimal("1000.00"),
			List.of("a"), List.of("p"), List.of("t"), List.of(1L)
		);
		BookDocument doc = BookDocument.builder().id(1L).build();
		when(repository.findById(1L)).thenReturn(Optional.of(doc));
		service.updateBook(req);
		verify(repository).save(doc);
	}

	@Nested
	@DisplayName("태그 수정시 엘라스틱에 저장된 도서 정보 수정")
	class UpdateTagNameTest {
		@Test
		@DisplayName("태그명 수정")
		void updateTagName() {
			Book book = Book.builder().id(1L).build();
			BookDocument doc = BookDocument.builder().id(1L).tagNames(List.of("old")).build();
			when(repository.findByIdIn(List.of(1L))).thenReturn(List.of(doc));
			service.updateTagName(List.of(book), "new", "old");
			verify(repository).save(argThat(d -> d.getTagNames().contains("new")));
		}

		@Test
		@DisplayName("태그 이름이 일치하지 않으면 업데이트되지 않음")
		void noMatchingTagName_noUpdate() {
			Book book = Book.builder().id(1L).build();
			BookDocument doc = BookDocument.builder().id(1L).tagNames(List.of("otherTag")).build();
			when(repository.findByIdIn(List.of(1L))).thenReturn(List.of(doc));

			service.updateTagName(List.of(book), "newTag", "nonExistentTag");

			verify(repository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("작가명 수정시 엘라스틱에 저장된 도서 정보 수정")
	class UpdateAuthorNameTest {

		@Test
		@DisplayName("작가명 수정")
		void updateAuthorName() {
			Book book = Book.builder().id(1L).build();
			BookDocument doc = BookDocument.builder().id(1L).authorNames(List.of("old")).build();
			when(repository.findByIdIn(List.of(1L))).thenReturn(List.of(doc));
			service.updateAuthorName(List.of(book), "new", "old");
			verify(repository).save(argThat(d -> d.getAuthorNames().contains("new")));
		}

		@Test
		@DisplayName("저자 이름이 일치하지 않으면 업데이트되지 않음")
		void noMatchingAuthorName_noUpdate() {
			Book book = Book.builder().id(1L).build();
			BookDocument doc = BookDocument.builder().id(1L).authorNames(List.of("someoneElse")).build();
			when(repository.findByIdIn(List.of(1L))).thenReturn(List.of(doc));

			service.updateAuthorName(List.of(book), "newAuthor", "nonExistentAuthor");

			verify(repository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("출판사명 수정시 엘라스틱에 저장된 도서 정보 수정")
	class UpdatePublisherNameTest {
		@Test
		@DisplayName("출판사명 수정")
		void updatePublisherName() {
			Book book = Book.builder().id(1L).build();
			BookDocument doc = BookDocument.builder().id(1L).publisherNames(List.of("old")).build();
			when(repository.findByIdIn(List.of(1L))).thenReturn(List.of(doc));
			service.updatePublisherName(List.of(book), "new", "old");
			verify(repository).save(argThat(d -> d.getPublisherNames().contains("new")));
		}

		@Test
		@DisplayName("출판사 이름이 일치하지 않으면 업데이트되지 않음")
		void noMatchingPublisherName_noUpdate() {
			Book book = Book.builder().id(1L).build();
			BookDocument doc = BookDocument.builder().id(1L).publisherNames(List.of("wrongPub")).build();
			when(repository.findByIdIn(List.of(1L))).thenReturn(List.of(doc));

			service.updatePublisherName(List.of(book), "newPub", "nonExistentPub");

			verify(repository, never()).save(any());
		}
	}

	@Test
	@DisplayName("도서 삭제 - 성공")
	void deleteBook_success() {
		Book book = Book.builder().id(1L).build();
		BookDocument doc = BookDocument.builder().id(1L).build();
		when(repository.findById(1L)).thenReturn(Optional.of(doc));
		service.deleteBook(book);
		verify(repository).delete(doc);
	}

	@Test
	@DisplayName("도서 삭제 - 예외 발생(엘라스틱 존재 하지 않음)")
	void deleteBook_notFound() {
		when(repository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.deleteBook(Book.builder().id(1L).build()))
			.isInstanceOf(ElasticsearchBookNotFoundException.class);
	}

	@Test
	@DisplayName("키워드 검색")
	void searchBooksByKeyword() {
		when(customRepository.searchBooksByKeyword("test", 0, 10)).thenReturn(Page.empty());
		Page<Book> result = service.searchBooksByKeyword("test", 0, 10);
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("키워드 + 정렬 검색")
	void searchBooksByKeywordAndSort() {
		when(customRepository.searchBooksByKeywordAndSort("test", BookSortType.NEWEST, 0, 10)).thenReturn(Page.empty());
		Page<Book> result = service.searchBooksByKeywordAndSort("test", BookSortType.NEWEST, 0, 10);
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("정렬만 검색")
	void searchBooksBySortOnly() {
		when(customRepository.searchBooksBySortOnly(BookSortType.RATING, 0, 10)).thenReturn(Page.empty());
		Page<Book> result = service.searchBooksBySortOnly(BookSortType.RATING, 0, 10);
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("카테고리 + 정렬 검색")
	void searchBooksByCategoryAndSort() {
		when(customRepository.searchBooksByCategoryAndSort(List.of(1L), BookSortType.POPULARITY, 0, 10)).thenReturn(Page.empty());
		Page<Book> result = service.searchBooksByCategoryAndSort(List.of(1L), BookSortType.POPULARITY, 0, 10);
		assertThat(result).isEmpty();
	}
}
