package shop.bluebooktle.backend.book.service;

import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookLikes;
import shop.bluebooktle.backend.book.jpa.BookLikesRepository;
import shop.bluebooktle.backend.book.jpa.BookRepository;
import shop.bluebooktle.backend.book.service.impl.BookLikesServiceImpl;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.auth.User;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class BookLikesServiceTest {

	@InjectMocks
	private BookLikesServiceImpl bookLikesService;

	@Mock
	private BookLikesRepository bookLikesRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName("도서 좋아요 성공")
	void like_success() {
		// given - 좋아요 요청 생성 및 레포지토리 반환 값 설정
		given(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).willReturn(false);

		Book book = mock(Book.class);
		given(book.getId()).willReturn(1L);
		given(bookRepository.findById(1L)).willReturn(Optional.of(book));

		User user = mock(User.class);
		given(user.getId()).willReturn(1L);
		given(userRepository.findById(1L)).willReturn(Optional.of(user));

		// when - 좋아요 요청 실행
		bookLikesService.like(1L, 1L);

		// then - 좋아요 요청 메서드 실행 확인
		verify(bookLikesRepository).save(argThat(bl -> {
			bl.getBook().getId().equals(1L);
			bl.getUser().getId().equals(1L);
			return true;
		}));
	}

	//
	// @Test
	// @DisplayName("좋아요 중복 예외 발생")
	// void BookLikesAlreadyCheckedTest() {
	// 	// given - 요청 객체 생성
	// 	BookLikesRequest request = BookLikesRequest.builder()
	// 		.bookId(1L)
	// 		.userId(1L)
	// 		.build();
	//
	// 	// when - 좋아요 이미 존재하도록 설정
	// 	when(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).thenReturn(true);
	// 	when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
	// 	when(userRepository.findById(1L)).thenReturn(Optional.of(user));
	//
	// 	// then - BookLikesAlreadyChecked 예외 발생 기대
	// 	assertThrows(BookLikesAlreadyChecked.class, () -> bookLikesService.like(request));
	// }
	//
	// @Test
	// @DisplayName("책 조회 불가 예외 발생")
	// void BookNotFoundExceptionTest() {
	// 	// given
	// 	BookLikesRequest request = BookLikesRequest.builder()
	// 		.bookId(1L)
	// 		.userId(1L)
	// 		.build();
	// 	when(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).thenReturn(false);
	// 	when(bookRepository.findById(1L)).thenReturn(Optional.empty()); // 도서 없음
	//
	// 	// when & then
	// 	assertThrows(BookNotFoundException.class, () -> bookLikesService.like(request));
	// }
	//
	// @Test
	// @DisplayName("사용자 조회 불가 예외 발생")
	// void UserNotFoundExceptionTest() {
	// 	// given
	// 	BookLikesRequest request = BookLikesRequest.builder()
	// 		.bookId(1L)
	// 		.userId(1L)
	// 		.build();
	//
	// 	when(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).thenReturn(false);
	// 	when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
	// 	when(userRepository.findById(1L)).thenReturn(Optional.empty()); // 유저 없음
	//
	// 	// when & then
	// 	assertThrows(UserNotFoundException.class, () -> bookLikesService.like(request));
	// }
	//
	@Test
	@DisplayName("도서 좋아요 삭제 - 성공")
	void unlike_success() {
		// given - 좋아요 요청 엔티티가 미리 있도록 설정
		Book book = mock(Book.class);
		when(book.getId()).thenReturn(1L);
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);

		BookLikes like = new BookLikes(book, user);
		given(bookLikesRepository.findByUser_IdAndBook_Id(1L, 1L)).willReturn(like);

		// when - 좋아요 요청 실행
		bookLikesService.unlike(1L, 1L);

		// then - 좋아요 요청 메서드 실행 확인
		verify(bookLikesRepository).delete(argThat(bl ->
			bl.getBook().getId().equals(1L) &&
				bl.getUser().getId().equals(1L)
		));
	}
	//
	// @Test
	// @DisplayName("좋아요 여부 확인")
	// void isLikedTest() {
	// 	//given - 요청 객체 생성
	// 	BookLikesRequest request = BookLikesRequest.builder()
	// 		.bookId(1L)
	// 		.userId(1L)
	// 		.build();
	// 	when(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).thenReturn(true); // 좋아요 존재
	// 	when(bookLikesRepository.countByBook_Id(1L)).thenReturn(10L); // 총 좋아요 수가 10개라 가정
	//
	// 	//when - 좋아요 여부 확인
	// 	BookLikesResponse response = bookLikesService.isLiked(request);
	//
	// 	//then
	// 	assertThat(response.getBookId()).isEqualTo(1L);
	// 	assertThat(response.isLiked()).isTrue();
	// 	assertThat(response.getCountLikes()).isEqualTo(10L);
	// }
	//
	// @Test
	// @DisplayName("도서 좋아요 수 조회")
	// void countLikesTest() {
	// 	// given - 좋아요 수가 5개인 도서에 대해 요청
	// 	BookLikesRequest request = BookLikesRequest.builder()
	// 		.bookId(1L)
	// 		.build();
	// 	when(bookLikesRepository.countByBook_Id(1L)).thenReturn(5L);
	//
	// 	// when - 좋아요 수 조회
	// 	BookLikesResponse response = bookLikesService.countLikes(request);
	//
	// 	// then
	// 	assertThat(response.getBookId()).isEqualTo(1L);
	// 	assertThat(response.isLiked()).isFalse(); // 로그인 안 한 사용자 기준
	// 	assertThat(response.getCountLikes()).isEqualTo(5L);
	// }
	//
	// @Test
	// @DisplayName("좋아요 누른 도서 조회")
	// void getBooksLikedByUserTest() {
	// 	// // given - 사용자가 좋아요한 BookLikes 리스트 반환
	// 	// BookLikesRequest request = BookLikesRequest.builder()
	// 	// 	.userId(1L)
	// 	// 	.build();
	// 	// when(bookLikesRepository.findBooksLikedByUser(1L)).thenReturn(List.of(book));
	// 	// when(book.getId()).thenReturn(1L);
	// 	// when(bookLikesRepository.countByBook_Id(1L)).thenReturn(1L);
	// 	//
	// 	// // when - 좋아요한 도서 목록 조회
	// 	// List<BookLikesResponse> responses = bookLikesService.getBooksLikedByUser(request);
	// 	//
	// 	// // then
	// 	// assertThat(responses.size()).isEqualTo(1);
	// 	// assertThat(responses.get(0).getBookId()).isEqualTo(1L);
	// }
}
