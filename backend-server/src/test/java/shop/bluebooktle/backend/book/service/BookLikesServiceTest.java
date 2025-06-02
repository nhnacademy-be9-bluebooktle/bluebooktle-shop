package shop.bluebooktle.backend.book.service;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookLikesRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.service.impl.BookLikesServiceImpl;
import shop.bluebooktle.backend.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class BookLikesServiceTest {
	//TODO 서비스 테스트 구현
	@InjectMocks
	private BookLikesServiceImpl bookLikesService;

	@Mock
	private BookLikesRepository bookLikesRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BookAuthorRepository bookAuthorRepository;

	@Mock
	private BookImgRepository bookImgRepository;

	private Author author1;

	@BeforeEach
	void setUp() {
		author1 = mock(Author.class);
		when(author1.getName()).thenReturn("작가1");
	}

	// @Test
	// @DisplayName("도서 좋아요 - 성공")
	// void like_success() {
	// 	// given - 좋아요 요청 생성 및 레포지토리 반환 값 설정
	// 	given(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).willReturn(false);
	//
	// 	Book book = mock(Book.class);
	// 	given(book.getId()).willReturn(1L);
	// 	given(bookRepository.findById(1L)).willReturn(Optional.of(book));
	//
	// 	User user = mock(User.class);
	// 	given(user.getId()).willReturn(1L);
	// 	given(userRepository.findById(1L)).willReturn(Optional.of(user));
	//
	// 	// when - 좋아요 요청 실행
	// 	bookLikesService.like(1L, 1L);
	//
	// 	// then - 좋아요 요청 메서드 실행 확인
	// 	verify(bookLikesRepository).save(argThat(bl -> {
	// 		bl.getBook().getId().equals(1L);
	// 		bl.getUser().getId().equals(1L);
	// 		return true;
	// 	}));
	// }
	//
	// @Test
	// @DisplayName("좋아요 중복 예외 발생 - 성공")
	// void BookLikesAlreadyChecked_exception() {
	// 	// given
	// 	given(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).willReturn(true);
	//
	// 	// when & then
	// 	assertThatThrownBy(() -> bookLikesService.like(1L, 1L))
	// 		.isInstanceOf(BookLikesAlreadyChecked.class);
	// 	verify(bookLikesRepository, never()).save(any());
	// }
	//
	// @Test
	// @DisplayName("책 조회 불가 예외 발생 - 성공")
	// void BookNotFound_exception() {
	// 	// given
	// 	given(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 99L)).willReturn(false);
	// 	given(bookRepository.findById(99L)).willReturn(Optional.empty());
	//
	// 	// when & then
	// 	assertThatThrownBy(() -> bookLikesService.like(99L, 1L))
	// 		.isInstanceOf(BookNotFoundException.class);
	// 	verify(bookLikesRepository, never()).save(any());
	// }
	//
	// @Test
	// @DisplayName("사용자 조회 불가 예외 발생 - 성공")
	// void UserNotFoundExceptionTest() {
	// 	// given
	// 	Book book = mock(Book.class);
	// 	given(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 5L)).willReturn(false);
	// 	given(bookRepository.findById(5L)).willReturn(Optional.of(book));
	// 	given(userRepository.findById(1L)).willReturn(Optional.empty());
	//
	// 	// when & then
	// 	assertThatThrownBy(() -> bookLikesService.like(5L, 1L))
	// 		.isInstanceOf(UserNotFoundException.class);
	// 	verify(bookLikesRepository, never()).save(any());
	// }
	//
	// @Test
	// @DisplayName("도서 좋아요 삭제 - 성공")
	// void unlike_success() {
	// 	// given - 좋아요 요청 엔티티가 미리 있도록 설정
	// 	Book book = mock(Book.class);
	// 	when(book.getId()).thenReturn(1L);
	// 	User user = mock(User.class);
	// 	when(user.getId()).thenReturn(1L);
	//
	// 	BookLikes like = new BookLikes(book, user);
	// 	given(bookLikesRepository.findByUser_IdAndBook_Id(1L, 1L)).willReturn(like);
	//
	// 	// when - 좋아요 요청 실행
	// 	bookLikesService.unlike(1L, 1L);
	//
	// 	// then - 좋아요 요청 메서드 실행 확인
	// 	verify(bookLikesRepository).delete(argThat(bl ->
	// 		bl.getBook().getId().equals(1L) &&
	// 			bl.getUser().getId().equals(1L)
	// 	));
	// }
	//
	// @Test
	// @DisplayName("도서 좋아요 여부 조회 - 성공")
	// void isLiked_success() {
	// 	//given - 요청 객체 생성
	// 	given(bookLikesRepository.existsByUser_IdAndBook_Id(1L, 1L)).willReturn(true);
	// 	given(bookLikesRepository.countByBook_Id(1L)).willReturn(10L);
	//
	// 	//when - 좋아요 여부 확인
	// 	BookLikesResponse response = bookLikesService.isLiked(1L, 1L);
	//
	// 	//then
	// 	assertThat(response.getBookId()).isEqualTo(1L);
	// 	assertThat(response.isLiked()).isTrue();
	// 	assertThat(response.getCountLikes()).isEqualTo(10L);
	// }
	//
	// @Test
	// @DisplayName("도서 좋아요 수 조회 - 성공")
	// void countLikes_success() {
	// 	// given - 좋아요 수가 5개인 도서에 대해 요청
	// 	given(bookLikesRepository.countByBook_Id(1L)).willReturn(5L);
	//
	// 	// when - 좋아요 수 조회
	// 	BookLikesResponse response = bookLikesService.countLikes(1L);
	//
	// 	// then
	// 	assertThat(response.getBookId()).isEqualTo(1L);
	// 	assertThat(response.isLiked()).isFalse(); // 로그인 안 한 사용자 기준
	// 	assertThat(response.getCountLikes()).isEqualTo(5L);
	// }

	// @Test
	// @DisplayName("좋아요 누른 도서 목록 조회 - 성공")
	// void getBooksLikedByUser_success() {
	// 	// given
	// 	Book book1 = mock(Book.class);
	// 	given(book1.getId()).willReturn(1L);
	// 	Book book3 = mock(Book.class);
	// 	given(book3.getId()).willReturn(3L);
	//
	// 	User user1 = mock(User.class);
	// 	given(user1.getId()).willReturn(10L);
	//
	// 	BookImg mockImg1 = mock(BookImg.class);
	// 	given(bookImgRepository.findFirstByBookIdAndIsThumbnailTrueOrderByIdAsc(1L)).willReturn(Optional.of(mockImg1));
	//
	// 	BookImg mockImg3 = mock(BookImg.class);
	// 	given(bookImgRepository.findFirstByBookIdAndIsThumbnailTrueOrderByIdAsc(3L)).willReturn(Optional.of(mockImg3));
	//
	// 	BookLikes like1 = new BookLikes(book1, user1);
	// 	BookLikes like3 = new BookLikes(book3, user1);
	//
	// 	given(bookLikesRepository.findAllByUser_Id(10L)).willReturn(List.of(like1, like3));
	// 	given(bookLikesRepository.countByBook_Id(1L)).willReturn(2L);
	// 	given(bookLikesRepository.countByBook_Id(3L)).willReturn(4L);
	// 	given(bookAuthorRepository.findAuthorsByBook(any())).willReturn(List.of(author1));
	//
	// 	UserDto userDto = UserDto.builder()
	// 		.id(10L)
	// 		.loginId("test")
	// 		.nickname("test")
	// 		.status(UserStatus.ACTIVE)
	// 		.type(UserType.USER)
	// 		.build();
	// 	UserPrincipal userPrincipal = new UserPrincipal(userDto);
	//
	// 	// when
	// 	List<BookLikesListResponse> responses = bookLikesService.getBooksLikedByUser(userPrincipal);
	//
	// 	// then
	// 	assertThat(responses).hasSize(2);
	// 	assertThat(responses).extracting("bookId").containsExactly(1L, 3L);
	// 	assertThat(responses).extracting("isLiked").containsOnly(true);
	// 	assertThat(responses).extracting("countLikes").containsExactly(2, 4);
	// }
}
