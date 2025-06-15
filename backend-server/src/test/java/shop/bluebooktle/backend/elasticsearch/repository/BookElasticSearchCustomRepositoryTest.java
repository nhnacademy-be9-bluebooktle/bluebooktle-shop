package shop.bluebooktle.backend.elasticsearch.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.elasticsearch.document.BookDocument;
import shop.bluebooktle.common.dto.book.BookSortType;

public class BookElasticSearchCustomRepositoryTest {
	@Mock
	private ElasticsearchOperations operations;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookElasticSearchCustomRepository repository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private List<BookDocument> createMockDocuments(int count) {
		return IntStream.range(1, count + 1)
			.mapToObj(i -> BookDocument.builder().id((long) i).build())
			.toList();
	}

	private List<Book> createMockBooks(List<Long> ids) {
		return ids.stream().map(id -> Book.builder().id(id).build()).toList();
	}

	private SearchHits<BookDocument> mockSearchHits(List<BookDocument> contentList) {
		List<SearchHit<BookDocument>> hits = contentList.stream()
			.map(doc -> new SearchHit<>(
				null,         // index
				doc.getId() != null ? doc.getId().toString() : null,  // id
				null,         // routing
				1.0f,         // score
				null,         // sortValues
				null,         // highlightFields
				null,         // innerHits
				null,         // nestedMetaData
				null,         // explanation
				null,         // matchedQueries
				doc           // content
			))
			.toList();

		return new SearchHitsImpl<>(
			hits.size(),                       // totalHits
			TotalHitsRelation.EQUAL_TO,        // totalHitsRelation
			1.0f,                               // maxScore
			Duration.ZERO,                     // executionDuration
			null,                              // scrollId
			null,                              // pointInTimeId
			hits,                              // searchHits
			null,                              // aggregations
			null,                              // suggest
			null                               // searchShardStatistics
		);
	}

	@Nested
	class SearchTests {

		@Test
		@DisplayName("키워드 + 정렬로 검색 성공")
		void searchBooksByKeywordAndSort_success() {
			List<BookDocument> documents = createMockDocuments(3);
			List<Long> ids = documents.stream().map(BookDocument::getId).toList();
			List<Book> books = createMockBooks(ids);

			when(operations.search(any(CriteriaQuery.class), eq(BookDocument.class)))
				.thenReturn(mockSearchHits(documents));
			when(bookRepository.findAllById(ids)).thenReturn(books);

			Page<Book> result = repository.searchBooksByKeywordAndSort("test", BookSortType.POPULARITY, 0, 10);

			assertThat(result.getContent()).hasSize(3);
		}

		@Test
		@DisplayName("키워드 검색만 성공")
		void searchBooksByKeyword_success() {
			List<BookDocument> documents = createMockDocuments(2);
			List<Long> ids = documents.stream().map(BookDocument::getId).toList();
			List<Book> books = createMockBooks(ids);

			when(operations.search(any(CriteriaQuery.class), eq(BookDocument.class)))
				.thenReturn(mockSearchHits(documents));
			when(bookRepository.findAllById(ids)).thenReturn(books);

			Page<Book> result = repository.searchBooksByKeyword("hello", 0, 5);

			assertThat(result.getContent()).hasSize(2);
		}

		@Test
		@DisplayName("정렬만 성공")
		void searchBooksBySortOnly_success() {
			List<BookDocument> documents = createMockDocuments(1);
			List<Long> ids = documents.stream().map(BookDocument::getId).toList();
			List<Book> books = createMockBooks(ids);

			when(operations.search(any(CriteriaQuery.class), eq(BookDocument.class)))
				.thenReturn(mockSearchHits(documents));
			when(bookRepository.findAllById(ids)).thenReturn(books);

			Page<Book> result = repository.searchBooksBySortOnly(BookSortType.RATING, 0, 1);

			assertThat(result.getContent()).hasSize(1);
		}

		@Test
		@DisplayName("카테고리 + 정렬로 검색 성공")
		void searchBooksByCategoryAndSort_success() {
			List<BookDocument> documents = createMockDocuments(2);
			List<Long> ids = documents.stream().map(BookDocument::getId).toList();
			List<Book> books = createMockBooks(ids);

			when(operations.search(any(CriteriaQuery.class), eq(BookDocument.class)))
				.thenReturn(mockSearchHits(documents));
			when(bookRepository.findAllById(ids)).thenReturn(books);

			Page<Book> result = repository.searchBooksByCategoryAndSort(List.of(1L, 2L), BookSortType.NEWEST, 0, 10);

			assertThat(result.getContent()).hasSize(2);
		}
	}

	@Test
	@DisplayName("모든 정렬 타입별 검색 동작 커버")
	void allSortTypes_should_be_covered() {
		for (BookSortType sortType : BookSortType.values()) {
			List<BookDocument> documents = createMockDocuments(1);
			List<Long> ids = documents.stream().map(BookDocument::getId).toList();
			List<Book> books = createMockBooks(ids);

			when(operations.search(any(CriteriaQuery.class), eq(BookDocument.class)))
				.thenReturn(mockSearchHits(documents));
			when(bookRepository.findAllById(ids)).thenReturn(books);

			Page<Book> result = repository.searchBooksBySortOnly(sortType, 0, 1);

			assertThat(result.getContent()).hasSize(1);
		}
	}

	@Test
	@DisplayName("정렬 타입이 null이면 default 정렬 동작")
	void sortType_null_should_use_default_sort() {
		List<BookDocument> documents = createMockDocuments(1);
		List<Long> ids = documents.stream().map(BookDocument::getId).toList();
		List<Book> books = createMockBooks(ids);

		when(operations.search(any(CriteriaQuery.class), eq(BookDocument.class)))
			.thenReturn(mockSearchHits(documents));
		when(bookRepository.findAllById(ids)).thenReturn(books);

		Page<Book> result = repository.searchBooksBySortOnly(null, 0, 1); // ✅ 핵심

		assertThat(result.getContent()).hasSize(1);
	}
}
