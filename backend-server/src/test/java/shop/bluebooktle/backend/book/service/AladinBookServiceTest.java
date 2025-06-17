package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.book.adapter.AladinAdaptor;
import shop.bluebooktle.backend.book.service.impl.AladinBookServiceImpl;
import shop.bluebooktle.common.dto.book.request.AladinBookItem;
import shop.bluebooktle.common.dto.book.response.AladinApiResponse;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;

@ExtendWith(MockitoExtension.class)
class AladinBookServiceTest {

	@Mock
	private AladinAdaptor aladinAdaptor;

	@InjectMocks
	private AladinBookServiceImpl aladinBookService;

	@Test
	@DisplayName("알라딘 도서 조회 성공")
	void searchBooks_Success() {

		String query = "query";
		int page = 1;
		int pageSize = 10;

		AladinBookItem aladinBookItem1 = AladinBookItem.builder()
			.title("title1")
			.pubDate("2025-06-12")
			.build();

		AladinBookItem aladinBookItem2 = AladinBookItem.builder()
			.title("title2")
			.pubDate("2025-06-13")
			.build();

		List<AladinBookItem> aladinBookItems = List.of(aladinBookItem1, aladinBookItem2);

		AladinApiResponse aladinApiResponse = AladinApiResponse.builder()
			.item(aladinBookItems)
			.build();

		when(aladinAdaptor.searchBooks(query, page, pageSize)).thenReturn(aladinApiResponse);

		List<AladinBookResponse> aladinBookResponses = aladinBookService.searchBooks(query, page, pageSize);

		assertThat(aladinBookResponses).hasSize(2);
		assertThat(aladinBookResponses.get(0).getTitle()).isEqualTo("title1");
		assertThat(aladinBookResponses.get(1).getTitle()).isEqualTo("title2");
	}

	@Test
	@DisplayName("알라딘 도서 조회 실패 - 비정상적인 인자값(1)")
	void searchBooks_IllegalArgument_1() {
		String query = "query";
		int page = 0;
		int pageSize = 0;

		assertThrows(IllegalArgumentException.class, () ->
			aladinBookService.searchBooks(query, page, pageSize)
		);

		verify(aladinAdaptor, never()).searchBooks(query, page, pageSize);
	}

	// 소나큐브 커버리지 보충용
	@Test
	@DisplayName("알라딘 도서 조회 실패 - 비정상적인 인자값(2)")
	void searchBooks_IllegalArgument_2() {
		String query = "query";
		int page = 1;
		int pageSize = 0;

		assertThrows(IllegalArgumentException.class, () ->
			aladinBookService.searchBooks(query, page, pageSize)
		);

		verify(aladinAdaptor, never()).searchBooks(query, page, pageSize);
	}

	@Test
	@DisplayName("알라딘 도서 조회 성공 - 디폴트 페이징")
	void searchBooks_Success_DefaultPaging() {

		String query = "query";

		AladinBookItem aladinBookItem1 = AladinBookItem.builder()
			.title("title1")
			.pubDate("2025-06-12")
			.build();

		AladinBookItem aladinBookItem2 = AladinBookItem.builder()
			.title("title2")
			.pubDate("2025-06-13")
			.build();

		List<AladinBookItem> aladinBookItems = List.of(aladinBookItem1, aladinBookItem2);

		AladinApiResponse aladinApiResponse = AladinApiResponse.builder()
			.item(aladinBookItems)
			.build();

		when(aladinAdaptor.searchBooks(query, 1, 10)).thenReturn(aladinApiResponse);

		List<AladinBookResponse> aladinBookResponses = aladinBookService.searchBooks(query);

		assertThat(aladinBookResponses).hasSize(2);
		assertThat(aladinBookResponses.get(0).getTitle()).isEqualTo("title1");
		assertThat(aladinBookResponses.get(1).getTitle()).isEqualTo("title2");
	}

	@Test
	@DisplayName("ISBN으로 도서 조회 성공")
	void getBookByIsbn_Success() {

		String isbn = "1234512345123";

		AladinBookItem aladinBookItem = AladinBookItem.builder()
			.title("title")
			.pubDate("2025-06-12")
			.isbn13(isbn)
			.build();

		List<AladinBookItem> aladinBookItems = Collections.singletonList(aladinBookItem);

		AladinApiResponse aladinApiResponse = AladinApiResponse.builder()
			.item(aladinBookItems)
			.build();

		when(aladinAdaptor.searchBooks(isbn, 1, 10)).thenReturn(aladinApiResponse);

		AladinBookResponse aladinBookResponse = aladinBookService.getBookByIsbn(isbn);

		assertEquals("title", aladinBookResponse.getTitle());
		assertEquals(isbn, aladinBookResponse.getIsbn());
	}
}