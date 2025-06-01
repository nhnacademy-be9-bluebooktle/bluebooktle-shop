package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.service.impl.BookAuthorServiceImpl;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.exception.book.AuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookAuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookAuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BookAuthorServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private BookAuthorRepository bookAuthorRepository;

	@InjectMocks
	private BookAuthorServiceImpl bookAuthorService;

	@Test
	@DisplayName("도서 작가 연결 성공")
	void registerBookAuthor_success() {
		Book book = Book.builder()
			.title("도서 제목")
			.build();
		ReflectionTestUtils.setField(book, "id", 1L);

		Author author = Author.builder()
			.name("작가 이름")
			.build();
		ReflectionTestUtils.setField(author, "id", 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(bookAuthorRepository.existsByBookAndAuthor(book, author)).thenReturn(false);

		bookAuthorService.registerBookAuthor(1L, 1L);

		verify(bookAuthorRepository, times(1)).save(any(BookAuthor.class));
	}

	@Test
	@DisplayName("작가가 존재하지 않는 경우")
	void registerBookAuthor_fail_authorNotFound() {

		when(authorRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AuthorNotFoundException.class, () -> bookAuthorService.registerBookAuthor(1L, 1L));
	}

	@Test
	@DisplayName("도서가 존재하지 않는 경우")
	void registerBookAuthor_fail_bookNotFound() {

		Author author = Author.builder()
			.name("작가 이름")
			.build();
		ReflectionTestUtils.setField(author, "id", 1L);

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> bookAuthorService.registerBookAuthor(1L, 1L));
	}

	@Test
	@DisplayName("이미 도서 작가가 연결된 경우")
	void registerBookAuthor_fail_alreadyRegistered() {
		Book book = Book.builder()
			.title("도서 제목")
			.build();
		ReflectionTestUtils.setField(book, "id", 1L);

		Author author = Author.builder()
			.name("작가 이름")
			.build();
		ReflectionTestUtils.setField(author, "id", 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(bookAuthorRepository.existsByBookAndAuthor(book, author)).thenReturn(true);

		assertThrows(BookAuthorAlreadyExistsException.class, () -> bookAuthorService.registerBookAuthor(1L, 1L));
	}

	@Test
	@DisplayName("도서에 여러 작가를 연결하는 경우")
	void registerBookAuthors() {
		Book book = Book.builder()
			.title("도서 제목")
			.build();
		ReflectionTestUtils.setField(book, "id", 1L);

		Author author1 = Author.builder()
			.name("홍길동")
			.build();
		ReflectionTestUtils.setField(author1, "id", 1L);

		Author author2 = Author.builder()
			.name("청길동")
			.build();
		ReflectionTestUtils.setField(author2, "id", 2L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
		when(authorRepository.findById(2L)).thenReturn(Optional.of(author2));

		List<Long> authorsId = List.of(1L, 2L);

		bookAuthorService.registerBookAuthor(1L, authorsId);

		verify(bookAuthorRepository, times(2)).save(any(BookAuthor.class));
	}

	@Test
	@DisplayName("도서 아이디로 작가 조회 성공")
	void getAuthorByBookId_success() {

		Book book = Book.builder()
			.build();
		ReflectionTestUtils.setField(book, "id", 1L);

		Author author = Author.builder()
			.name("홍길동")
			.build();
		ReflectionTestUtils.setField(author, "id", 1L);
		ReflectionTestUtils.setField(author, "createdAt", LocalDateTime.of(2025, 6, 1, 0, 0));

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookAuthorRepository.findAuthorsByBook(book)).thenReturn(List.of(author));

		List<AuthorResponse> authorResponses = bookAuthorService.getAuthorByBookId(book.getId());

		assertThat(authorResponses).hasSize(1);
		assertThat(authorResponses.get(0).getId()).isEqualTo(1L);
		assertThat(authorResponses.get(0).getName()).isEqualTo("홍길동");
		assertThat(authorResponses.get(0).getCreatedAt()).isEqualTo("2025-06-01T00:00:00");
	}

	@Test
	@DisplayName("도서 아이디로 작가 조회 시 유효한 도서 아이디가 아닌 경우")
	void getAuthorByBookId_fail_invalidBookId() {

		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> bookAuthorService.getAuthorByBookId(1L));
	}

	@Test
	@DisplayName("작가 아이디로 도서 조회 성공")
	void getBookByAuthorId_success() {

		Book book = Book.builder()
			.build();
		ReflectionTestUtils.setField(book, "id", 1L);

		Author author = Author.builder()
			.build();
		ReflectionTestUtils.setField(author, "id", 1L);

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(bookAuthorRepository.findBooksByAuthor(author)).thenReturn(List.of(book));

		List<BookInfoResponse> bookInfoResponses = bookAuthorService.getBookByAuthorId(1L);

		assertThat(bookInfoResponses).hasSize(1);
		assertThat(bookInfoResponses.get(0).bookId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("작가 아이디로 도서 조회 시 유효한 작가 아이디가 아닌 경우")
	void getBookByAuthorId_fail_invalidAuthorId() {

		when(authorRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AuthorNotFoundException.class, () -> bookAuthorService.getBookByAuthorId(1L));
	}

	// 도서-작가 관계 삭제 성공
	@Test
	@DisplayName("도서 작가 관계 삭제 성공")
	void deleteBookAuthor_success () {

		Author author = Author.builder()
			.name("홍길동")
			.build();
		ReflectionTestUtils.setField(author, "id", 1L);

		Book book = Book.builder()
			.title("테스트 제목")
			.build();
		ReflectionTestUtils.setField(book, "id", 1L);

		BookAuthor bookAuthor = BookAuthor.builder()
			.book(book)
			.author(author)
			.build();

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookAuthorRepository.findByBookAndAuthor(book,author)).thenReturn(Optional.of(bookAuthor));

		bookAuthorService.deleteBookAuthor(1L, 1L);

		verify(bookAuthorRepository, times(1)).delete(bookAuthor);
	}

	// 유효한 작가가 아닌 경우
	@Test
	@DisplayName("도서 작가 관계 삭제 시 유효한 작가가 아닌 경우")
	void deleteBookAuthor_fail_invalidAuthorId () {
		when(authorRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(AuthorNotFoundException.class, () -> bookAuthorService.deleteBookAuthor(1L, 1L));
	}

	// 유효한 도서가 아닌 경우
	@Test
	@DisplayName("도서 작가 관계 삭제 시 유효한 도서가 아닌 경우")
	void deleteBookAuthor_fail_invalidBookId () {

		Author author = Author.builder()
			.build();
		ReflectionTestUtils.setField(author, "id", 1L);

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(BookNotFoundException.class, () -> bookAuthorService.deleteBookAuthor(1L, 1L));
	}

	// 유효한 관계가 아닌 경우
	@Test
	@DisplayName("도서 작가 관계 삭제 시 유효한 관계가 아닌 경우")
	void deleteBookAuthor_fail_invalidBookAuthorId () {
		Author author = Author.builder()
			.build();
		ReflectionTestUtils.setField(author, "id", 1L);

		Book book = Book.builder()
			.build();
		ReflectionTestUtils.setField(book, "id", 1L);

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookAuthorRepository.findByBookAndAuthor(book,author)).thenReturn(Optional.empty());

		assertThrows(BookAuthorNotFoundException.class, () -> bookAuthorService.deleteBookAuthor(1L, 1L));
	}
}
