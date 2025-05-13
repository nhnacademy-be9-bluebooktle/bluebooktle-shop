package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import shop.bluebooktle.backend.book.dto.request.BookLikesRequest;
import shop.bluebooktle.backend.book.dto.response.BookLikesResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookLikes;
import shop.bluebooktle.backend.book.repository.BookLikesRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.service.impl.BookLikesServiceImpl;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookLikesAlreadyChecked;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

public class BookLikesServiceTest {

	@InjectMocks
	private BookLikesServiceImpl bookLikesService;

	@Mock
	private BookLikesRepository bookLikesRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private UserRepository userRepository;

	private Book book;
	private User user;

	/** 사전 설정 */
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this); // Mockito 어노테이션 초기화
		book = mock(Book.class); // Book 객체를 mock 으로 생성
		user = mock(User.class); // User 객체를 mock 으로 생성
		when(book.getId()).thenReturn(1L); // ID 값 설정
		when(user.getId()).thenReturn(1L);
	}

	@Test
	@DisplayName("도서 좋아요 성공")
	void likeBookTest() {
		// given - 좋아요 요청 생성 및 레포지토리 반환 값 설정
		BookLikesRequest request = BookLikesRequest.builder()
			.bookId(1L)
			.userId(1L)
			.build();
		when(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).thenReturn(false);
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		// when - 좋아요 요청 실행
		bookLikesService.like(request);

		// then - 좋아요 요청 메서드 실행 확인
		verify(bookLikesRepository).save(any(BookLikes.class));
	}

	@Test
	@DisplayName("좋아요 중복 예외 발생")
	void BookLikesAlreadyCheckedTest() {
		// given - 요청 객체 생성
		BookLikesRequest request = BookLikesRequest.builder()
			.bookId(1L)
			.userId(1L)
			.build();

		// when - 좋아요 이미 존재하도록 설정
		when(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).thenReturn(true);
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		// then - BookLikesAlreadyChecked 예외 발생 기대
		assertThrows(BookLikesAlreadyChecked.class, () -> bookLikesService.like(request));
	}

	@Test
	@DisplayName("책 조회 불가 예외 발생")
	void BookNotFoundExceptionTest() {
		// given
		BookLikesRequest request = BookLikesRequest.builder()
			.bookId(1L)
			.userId(1L)
			.build();
		when(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).thenReturn(false);
		when(bookRepository.findById(1L)).thenReturn(Optional.empty()); // 도서 없음

		// when & then
		assertThrows(BookNotFoundException.class, () -> bookLikesService.like(request));
	}

	@Test
	@DisplayName("사용자 조회 불가 예외 발생")
	void UserNotFoundExceptionTest() {
		// given
		BookLikesRequest request = BookLikesRequest.builder()
			.bookId(1L)
			.userId(1L)
			.build();

		when(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).thenReturn(false);
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(userRepository.findById(1L)).thenReturn(Optional.empty()); // 유저 없음

		// when & then
		assertThrows(UserNotFoundException.class, () -> bookLikesService.like(request));
	}

	@Test
	@DisplayName("도서 좋아요 취소 성공")
	void unlikeBookTest() {
		// given - 좋아요 요청 엔티티가 미리 있도록 설정
		BookLikesRequest request = BookLikesRequest.builder()
			.bookId(1L)
			.userId(1L)
			.build();
		BookLikes like = mock(BookLikes.class);
		when(bookLikesRepository.findByUser_IdAndBook_Id(1L, 1L)).thenReturn(like);

		// when - 좋아요 요청 실행
		bookLikesService.unlike(request);

		// then - 좋아요 요청 메서드 실행 확인
		verify(bookLikesRepository).delete(like);
	}

	@Test
	@DisplayName("좋아요 여부 확인")
	void isLikedTest() {
		//given - 요청 객체 생성
		BookLikesRequest request = BookLikesRequest.builder()
			.bookId(1L)
			.userId(1L)
			.build();
		when(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).thenReturn(true); // 좋아요 존재
		when(bookLikesRepository.countByBook_Id(1L)).thenReturn(10L); // 총 좋아요 수가 10개라 가정

		//when - 좋아요 여부 확인
		BookLikesResponse response = bookLikesService.isLiked(request);

		//then
		assertThat(response.getBookId()).isEqualTo(1L);
		assertThat(response.isLiked()).isTrue();
		assertThat(response.getCountLikes()).isEqualTo(10L);
	}

	@Test
	@DisplayName("도서 좋아요 수 조회")
	void countLikesTest() {
		// given - 좋아요 수가 5개인 도서에 대해 요청
		BookLikesRequest request = BookLikesRequest.builder()
			.bookId(1L)
			.build();
		when(bookLikesRepository.countByBook_Id(1L)).thenReturn(5L);

		// when - 좋아요 수 조회
		BookLikesResponse response = bookLikesService.countLikes(request);

		// then
		assertThat(response.getBookId()).isEqualTo(1L);
		assertThat(response.isLiked()).isFalse(); // 로그인 안 한 사용자 기준
		assertThat(response.getCountLikes()).isEqualTo(5L);
	}

	@Test
	@DisplayName("좋아요 누른 도서 조회")
	void getBooksLikedByUserTest() {
		// given - 사용자가 좋아요한 BookLikes 리스트 반환
		BookLikesRequest request = BookLikesRequest.builder()
			.userId(1L)
			.build();
		when(bookLikesRepository.findBooksLikedByUser(1L)).thenReturn(List.of(book));
		when(book.getId()).thenReturn(1L);
		when(bookLikesRepository.countByBook_Id(1L)).thenReturn(1L);

		// when - 좋아요한 도서 목록 조회
		List<BookLikesResponse> responses = bookLikesService.getBooksLikedByUser(request);

		// then
		assertThat(responses.size()).isEqualTo(1);
		assertThat(responses.get(0).getBookId()).isEqualTo(1L);
	}
}
