// package shop.bluebooktle.backend.book.service;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.time.LocalDate;
// import java.util.Collections;
// import java.util.List;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import shop.bluebooktle.backend.book.adapter.AladinAdaptor;
// import shop.bluebooktle.backend.book.service.impl.AladinBookServiceImpl;
// import shop.bluebooktle.common.dto.book.request.AladinBookItem;
// import shop.bluebooktle.common.dto.book.response.AladinApiResponse;
// import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
// import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;
//

// 서비스 로직 변경으로 인한 수정 필요

// @ExtendWith(MockitoExtension.class)
// public class AladinBookServiceTest {
//
// 	@Mock
// 	private AladinAdaptor aladinAdaptor;
//
// 	@InjectMocks
// 	private AladinBookServiceImpl aladinBookService;
//
// 	@Test
// 	@DisplayName("알라딘 응답을 내부 응답으로 변환 성공")
// 	void searchBooks_Success() {
// 		AladinBookItem aladinBookItem1 = AladinBookItem.builder()
// 			.title("테스트 제목1")
// 			.author("홍길동")
// 			.pubDate("2025-05-29")
// 			.build();
//
// 		AladinBookItem aladinBookItem2 = AladinBookItem.builder()
// 			.title("테스트 제목2")
// 			.author("청길동")
// 			.pubDate("2025-05-30")
// 			.build();
//
// 		AladinApiResponse aladinApiResponse = AladinApiResponse.builder()
// 			.item(List.of(aladinBookItem1, aladinBookItem2))
// 			.build();
//
// 		when(aladinAdaptor.searchBooks("테스트 키워드")).thenReturn(aladinApiResponse);
//
// 		List<AladinBookResponse> aladinBookResponses = aladinBookService.searchBooks("테스트 키워드");
//
// 		assertThat(aladinBookResponses).hasSize(2);
// 		assertThat(aladinBookResponses.get(0).getTitle()).isEqualTo("테스트 제목1");
// 		assertThat(aladinBookResponses.get(1).getTitle()).isEqualTo("테스트 제목2");
// 	}
//
// 	@Test
// 	@DisplayName("알라딘 응답이 빈 리스트인 경우 : 빈 값을 반환")
// 	void searchBooks_EmptyResponse() {
// 		AladinApiResponse aladinApiResponse = AladinApiResponse.builder()
// 			.item(Collections.emptyList())
// 			.build();
//
// 		when(aladinAdaptor.searchBooks("없는 책")).thenReturn(aladinApiResponse);
//
// 		List<AladinBookResponse> aladinBookResponses = aladinBookService.searchBooks("없는 책");
//
// 		assertThat(aladinBookResponses).isEmpty();
// 	}
//
// 	@Test
// 	@DisplayName("알라딘 응답이 null인 경우 : 예외 발생")
// 	void searchBooks_NullResponse() {
//
// 		when(aladinAdaptor.searchBooks("error")).thenReturn(null);
//
// 		assertThatThrownBy(() -> aladinBookService.searchBooks("error"))
// 			.isInstanceOf(AladinBookNotFoundException.class);
// 	}
//
// 	@Test
// 	@DisplayName("ISBN 검색 시 첫 번째 결과만 반환")
// 	void getBookByIsbn_ReturnsFirstBook() {
// 		AladinBookItem aladinBookItem1 = AladinBookItem.builder()
// 			.title("테스트 제목1")
// 			.author("홍길동")
// 			.pubDate("2025-05-29")
// 			.build();
//
// 		AladinBookItem aladinBookItem2 = AladinBookItem.builder()
// 			.title("테스트 제목2")
// 			.author("청길동")
// 			.pubDate("2025-05-30")
// 			.build();
//
// 		AladinApiResponse aladinApiResponse = AladinApiResponse.builder()
// 			.item(List.of(aladinBookItem1, aladinBookItem2))
// 			.build();
//
// 		when(aladinAdaptor.searchBooks("1234512345123"))
// 			.thenReturn(aladinApiResponse);
//
// 		AladinBookResponse aladinBookResponse = aladinBookService.getBookByIsbn("1234512345123");
//
// 		assertThat(aladinBookResponse.getTitle()).isEqualTo("테스트 제목1");
// 		assertThat(aladinBookResponse.getAuthor()).isEqualTo("홍길동");
// 		assertThat(aladinBookResponse.getPublishDate().toLocalDate()).isEqualTo(LocalDate.parse("2025-05-29"));
// 	}
//
// }