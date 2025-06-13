package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.exception.book.AuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookAuthorAlreadyExistsException;
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
	@DisplayName("도서에 연결된 작가 교체 성공")
	void updateBookAuthor_success() {

		Book book = Book.builder()
			.id(1L)
			.build();

		Author existingAuthor1 = Author.builder().build();
		ReflectionTestUtils.setField(existingAuthor1, "id", 1L);

		Author existingAuthor2 = Author.builder().build();
		ReflectionTestUtils.setField(existingAuthor2, "id", 2L);

		Author replacedAuthor1 = Author.builder().build();
		ReflectionTestUtils.setField(replacedAuthor1, "id", 3L);

		Author replacedAuthor2 = Author.builder().build();
		ReflectionTestUtils.setField(replacedAuthor2, "id", 4L);

		BookAuthor bookAuthor1 = BookAuthor.builder()
			.book(book)
			.author(existingAuthor1)
			.build();

		BookAuthor bookAuthor2 = BookAuthor.builder()
			.book(book)
			.author(existingAuthor2)
			.build();

		List<BookAuthor> existingBookAuthors = List.of(bookAuthor1, bookAuthor2);

		when(bookAuthorRepository.findByBookId(1L)).thenReturn(existingBookAuthors);
		when(authorRepository.findById(3L)).thenReturn(Optional.of(replacedAuthor1));
		when(authorRepository.findById(4L)).thenReturn(Optional.of(replacedAuthor2));
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookAuthorRepository.existsByBookAndAuthor(book, replacedAuthor1)).thenReturn(false);
		when(bookAuthorRepository.existsByBookAndAuthor(book, replacedAuthor2)).thenReturn(false);

		List<AuthorResponse> authorResponses = bookAuthorService.updateBookAuthor(1L, List.of(3L, 4L));

		verify(bookAuthorRepository, times(1)).deleteAll(existingBookAuthors);
		assertEquals(2, authorResponses.size());
		assertThat(authorResponses.get(0).getId()).isEqualTo(3L);
		assertThat(authorResponses.get(1).getId()).isEqualTo(4L);
	}
}
