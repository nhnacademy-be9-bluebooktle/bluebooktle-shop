package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.MyPageBookLikesRepository;
import shop.bluebooktle.frontend.service.impl.MyPageBookLikesServiceImpl;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class MyPageBookLikesServiceTest {

	@InjectMocks
	private MyPageBookLikesServiceImpl myPageBookLikesService;

	@Mock
	private MyPageBookLikesRepository myPageBookLikesRepository;

	@Test
	@DisplayName("도서 좋아요 목록 조회 - 성공")
	void getMyPageBookLikes_success() {
		// given
		List<BookLikesListResponse> allLikes = List.of(
			BookLikesListResponse.builder()
				.bookId(1L)
				.bookName("도서1")
				.authorName(List.of("작가1"))
				.imgUrl("http://book1.png")
				.price(new BigDecimal("10000"))
				.build(),
			BookLikesListResponse.builder()
				.bookId(2L)
				.bookName("도서2")
				.authorName(List.of("작가2"))
				.imgUrl("http://book2.png")
				.price(new BigDecimal("15000"))
				.build()
		);
		given(myPageBookLikesRepository.getMyPageBookLikes()).willReturn(allLikes);

		int page = 0;
		int size = 1;
		Pageable pageable = PageRequest.of(page, size);
		Page<BookLikesListResponse> expectedPage = new PageImpl<>(allLikes.subList(0, 1), pageable, allLikes.size());

		// when
		PaginationData<BookLikesListResponse> result = myPageBookLikesService.getMyPageBookLikes(page, size);

		// then
		assertThat(result.getPagination().getTotalElements()).isEqualTo(2);
		assertThat(result.getPagination().getCurrentPage()).isEqualTo(0);
		assertThat(result.getPagination().getPageSize()).isEqualTo(1);
	}

	@Test
	@DisplayName("도서 좋아요 해제 - 성공")
	void unlike_success() {
		// given
		Long bookId = 1L;
		willDoNothing().given(myPageBookLikesRepository).unlike(bookId);

		// when
		myPageBookLikesService.unlike(bookId);

		// then
		then(myPageBookLikesRepository).should().unlike(bookId);
	}

	@Test
	@DisplayName("전체 좋아요가 없는 경우 – 빈 페이지 반환")
	void getMyPageBookLikes_emptyList() {
		// given: 좋아요 내역이 전혀 없음
		given(myPageBookLikesRepository.getMyPageBookLikes()).willReturn(List.of());

		int page = 0;
		int size = 5;

		// when
		PaginationData<BookLikesListResponse> result = myPageBookLikesService.getMyPageBookLikes(page, size);

		// then
		assertThat(result.getPagination().getTotalElements()).isEqualTo(0);   // 전체 0건
		assertThat(result.getContent().size()).isEqualTo(0);                 // 내용도 0건
	}

	@Test
	@DisplayName("페이지 범위를 벗어난 요청 – 내용이 비어 있음")
	void getMyPageBookLikes_pageOutOfRange() {
		// given: 총 2건의 좋아요가 존재
		List<BookLikesListResponse> allLikes = List.of(
			BookLikesListResponse.builder()
				.bookId(1L)
				.bookName("도서1")
				.authorName(List.of("작가1"))
				.imgUrl("http://book1.png")
				.price(new BigDecimal("10000"))
				.build(),
			BookLikesListResponse.builder()
				.bookId(2L)
				.bookName("도서2")
				.authorName(List.of("작가2"))
				.imgUrl("http://book2.png")
				.price(new BigDecimal("15000"))
				.build()
		);
		given(myPageBookLikesRepository.getMyPageBookLikes()).willReturn(allLikes);

		int page = 3;   // start = page*size = 6  > likesList.size() = 2
		int size = 2;

		// when
		PaginationData<BookLikesListResponse> result = myPageBookLikesService.getMyPageBookLikes(page, size);

		// then
		assertThat(result.getPagination().getTotalElements()).isEqualTo(2);  // 전체 2건
		assertThat(result.getPagination().getCurrentPage()).isEqualTo(3);    // 요청한 page
		assertThat(result.getContent().size()).isEqualTo(0);                 // 내용은 비어 있어야 함
	}
}
