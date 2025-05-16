package shop.bluebooktle.backend.book.service.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.query.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.dto.response.author.AuthorResponse;
import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.service.BookAuthorService;

@Service
@RequiredArgsConstructor
public class BookAuthorServiceImpl implements BookAuthorService {

	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;
	private final BookAuthorRepository bookAuthorRepository;

	@Transactional
	@Override
	public void registerBookAuthor(Long bookId, Long authorId) {

		if (bookId == null || authorId == null) {
			throw new RuntimeException(); // TODO #1
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException()); // TODO #2
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException()); // TODO #3

		if (bookAuthorRepository.existsByBookAndAuthor(book, author)) {
			throw new RuntimeException(); // TODO #4
		}
		BookAuthor bookAuthor = BookAuthor.builder()
			.book(book)
			.author(author)
			.build();

		bookAuthorRepository.save(bookAuthor);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AuthorResponse> getAuthorByBookId(Long bookId) {

		if (bookId == null) {
			throw new RuntimeException(); // TODO #5
		}

		Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException()); // TODO #6

		List<Author> authors = bookAuthorRepository.findAuthorsByBook(book);

		return authors.stream()
			.map(author -> AuthorResponse.builder()
				.id(author.getId())
				.name(author.getName())
				.description(author.getDescription())
				.authorKey(author.getAuthorKey())
				.createdAt(author.getCreatedAt())
				.build())
			.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookInfoResponse> getBookByAuthorId(Long authorId) {
		if (authorId == null) {
			throw new RuntimeException(); // TODO #7
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException()); // TODO #8

		List<Book> books = bookAuthorRepository.findBooksByAuthor(author);

		return books.stream()
			.map(book -> new BookInfoResponse(book.getId()))
			.toList();
	}

	@Transactional
	@Override
	public void deleteBookAuthor(Long bookId, Long authorId) {

		if (bookId == null || authorId == null) {
			throw new RuntimeException(); // TODO #9
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException()); // TODO #10
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException()); // TODO #11

		BookAuthor bookAuthor = bookAuthorRepository.findByBookAndAuthor(book, author)
			.orElseThrow(() -> new RuntimeException()); // TODO #12

		bookAuthorRepository.delete(bookAuthor);
	}
}
