package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.BookLikes;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookLikesRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.service.impl.BookLikesServiceImpl;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.common.dto.book.response.BookLikesResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookLikesAlreadyChecked;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@ExtendWith(MockitoExtension.class)
@Slf4j
class BookLikesServiceTest {
	// 할 일 : 서비스 테스트 구현
	@InjectMocks
	private BookLikesServiceImpl bookLikesService;

	@Mock
	private BookLikesRepository bookLikesRepository;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private BookImgRepository bookImgRepository;
	@Mock
	private ImgRepository imgRepository;
	@Mock
	private BookAuthorRepository bookAuthorRepository;
	@Mock
	private BookSaleInfoRepository bookSaleInfoRepository;

	@Test
	@DisplayName("도서 좋아요 - 성공")
	void addLike() {
		// given
		Long bookId = 1L;
		Long userId = 10L;

		Book book = mock(Book.class);
		User user = mock(User.class);

		given(bookLikesRepository.existsByUser_IdAndBook_Id(userId, bookId)).willReturn(false);
		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		// when
		bookLikesService.like(bookId, userId);

		// then
		verify(bookLikesRepository).save(any(BookLikes.class));
	}

	@Test
	@DisplayName("도서 좋아요 등록 - 이미 등록된 경우 예외 발생")
	void BookLikesAlreadyCheckedException() {
		// given
		Long bookId = 1L;
		Long userId = 10L;
		given(bookLikesRepository.existsByUser_IdAndBook_Id(userId, bookId)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> bookLikesService.like(bookId, userId))
			.isInstanceOf(BookLikesAlreadyChecked.class);
	}

	@Test
	@DisplayName("도서 좋아요 등록 - 도서 찾기 예외 발생")
	void BookNotFoundException() {
		// given
		Long bookId = 100L;
		Long userId = 10L;
		given(bookLikesRepository.existsByUser_IdAndBook_Id(userId, bookId)).willReturn(false);
		given(bookRepository.findById(bookId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> bookLikesService.like(bookId, userId))
			.isInstanceOf(BookNotFoundException.class);
	}

	@Test
	@DisplayName("도서 좋아요 등록 - 회원 찾기 예외 발생")
	void UserNotFoundException() {
		// given
		Long bookId = 100L;
		Long userId = 10L;
		Book book = mock(Book.class);
		given(bookLikesRepository.existsByUser_IdAndBook_Id(userId, bookId)).willReturn(false);
		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
		given(userRepository.findById(userId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> bookLikesService.like(bookId, userId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("도서 좋아요 수 조회 - 성공")
	void countLikes() {
		//given
		Long bookId = 1L;
		given(bookLikesRepository.countByBook_Id(bookId)).willReturn(10L);

		// when
		BookLikesResponse result = bookLikesService.countLikes(bookId);

		// then
		assertThat(result.getBookId()).isEqualTo(bookId);
		assertThat(result.getCountLikes()).isEqualTo(10L);
		assertThat(result.isLiked()).isFalse();
	}

	@Test
	@DisplayName("도서 좋아요 상태 조회 - 성공")
	void isLiked() {
		// given
		Long bookId = 1L;
		Long userId = 10L;
		given(bookLikesRepository.existsByUser_IdAndBook_Id(userId, bookId)).willReturn(true);
		given(bookLikesRepository.countByBook_Id(bookId)).willReturn(5L);

		// when
		BookLikesResponse result = bookLikesService.isLiked(bookId, userId);

		// then
		assertThat(result.getBookId()).isEqualTo(1L);
		assertThat(result.isLiked()).isTrue();
		assertThat(result.getCountLikes()).isEqualTo(5L);
	}

	@Test
	@DisplayName("도서 좋아요 삭제 - 성공")
	void deleteLikes() {
		// given
		Long bookId = 1L;
		Long userId = 10L;
		BookLikes mockBookLikes = mock(BookLikes.class);
		given(bookLikesRepository.findByUser_IdAndBook_Id(userId, bookId)).willReturn(mockBookLikes);

		// when
		bookLikesService.unlike(bookId, userId);

		// then
		verify(bookLikesRepository).delete(mockBookLikes);
	}

	@Test
	@DisplayName("도서 좋아요 삭제 - 등록되지 않은 좋아요 삭제 불가")
	void deleteNotExistLikes() {
		// given
		Long bookId = 1L;
		Long userId = 10L;
		given(bookLikesRepository.findByUser_IdAndBook_Id(userId, bookId)).willReturn(null);

		// when
		bookLikesService.unlike(bookId, userId);

		// then
		verify(bookLikesRepository, never()).delete(any(BookLikes.class));
	}

	@Test
	@DisplayName("좋아요 누른 도서 목록 조회 - 성공")
	void getBooksLikedByUser() {
		// given
		Long bookId = 1L;
		Long userId = 10L;
		String title = "녹나무의 파수꾼";
		List<String> authorNames = List.of("히가시노 게이고", "양윤옥");
		String thumbnailUrl = "https://image.aladin.co.kr/product/23456/86/cover500/k362638685_1.jpg";
		BigDecimal price = BigDecimal.valueOf(16020);

		// Mock 객체 생성
		Book book = mock(Book.class);
		given(book.getId()).willReturn(bookId);
		given(book.getTitle()).willReturn(title);

		BookLikes bookLikes = mock(BookLikes.class);
		given(bookLikes.getBook()).willReturn(book);

		BookAuthor author1 = mock(BookAuthor.class);
		BookAuthor author2 = mock(BookAuthor.class);
		Author a1 = mock(Author.class);
		Author a2 = mock(Author.class);
		given(a1.getName()).willReturn("히가시노 게이고");
		given(a2.getName()).willReturn("양윤옥");
		given(author1.getAuthor()).willReturn(a1);
		given(author2.getAuthor()).willReturn(a2);

		BookImg bookImg = mock(BookImg.class);
		Img img = mock(Img.class);
		given(bookImg.getImg()).willReturn(img);
		given(img.getId()).willReturn(7L);
		given(img.getImgUrl()).willReturn(thumbnailUrl);

		BookSaleInfo bookSaleInfo = mock(BookSaleInfo.class);
		given(bookSaleInfo.getPrice()).willReturn(price);

		// 각 리포지토리 응답 설정
		given(bookLikesRepository.findAllByUser_Id(userId))
			.willReturn(List.of(bookLikes));

		given(bookAuthorRepository.findByBook_Id(bookId))
			.willReturn(List.of(author1, author2));

		given(bookImgRepository.findFirstByBookIdAndIsThumbnailTrueOrderByIdAsc(bookId))
			.willReturn(Optional.of(bookImg));

		given(imgRepository.findById(7L))
			.willReturn(Optional.of(img));

		given(bookSaleInfoRepository.findByBookId(bookId))
			.willReturn(Optional.of(bookSaleInfo));

		// when
		List<BookLikesListResponse> result = bookLikesService.getBooksLikedByUser(userId);

		// then
		assertThat(result).hasSize(1);
		BookLikesListResponse dto = result.get(0);
		assertThat(dto.getBookId()).isEqualTo(bookId);
		assertThat(dto.getBookName()).isEqualTo(title);
		assertThat(dto.getAuthorName()).containsExactlyInAnyOrderElementsOf(authorNames);
		assertThat(dto.getImgUrl()).isEqualTo(thumbnailUrl);
		assertThat(dto.getPrice()).isEqualTo(price);
	}
}
