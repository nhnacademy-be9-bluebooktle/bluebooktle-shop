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
import shop.bluebooktle.common.exception.book.AuthorIdNullException;
import shop.bluebooktle.common.exception.book.AuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookAuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookAuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookIdNullException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class BookAuthorServiceImpl implements BookAuthorService {

	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;
	private final BookAuthorRepository bookAuthorRepository;

	@Override
	public void registerBookAuthor(Long bookId, Long authorId) {
		if (bookId == null) {
			throw new BookIdNullException();
		}
		if (authorId == null) {
			throw new AuthorIdNullException();
		}

		Author author = authorRepository.findById(authorId)
			.orElseThrow(() -> new AuthorNotFoundException(authorId));
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException());

		if (bookAuthorRepository.existsByBookAndAuthor(book, author)) {
			throw new BookAuthorAlreadyExistsException(bookId, authorId);
		}

		BookAuthor ba = BookAuthor.builder()
			.book(book)
			.author(author)
			.build();
		bookAuthorRepository.save(ba);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AuthorResponse> getAuthorByBookId(Long bookId) {
		if (bookId == null) {
			throw new BookIdNullException();
		}
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException());

		return bookAuthorRepository.findAuthorsByBook(book).stream()
			.map(a -> AuthorResponse.builder()
				.id(a.getId())
				.name(a.getName())
				.description(a.getDescription())
				.authorKey(a.getAuthorKey())
				.createdAt(a.getCreatedAt())
				.build())
			.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookInfoResponse> getBookByAuthorId(Long authorId) {
		if (authorId == null) {
			throw new AuthorIdNullException();
		}
		Author author = authorRepository.findById(authorId)
			.orElseThrow(() -> new AuthorNotFoundException(authorId));

		return bookAuthorRepository.findBooksByAuthor(author).stream()
			.map(b -> new BookInfoResponse(b.getId()))
			.toList();
	}

	@Override
	public void deleteBookAuthor(Long bookId, Long authorId) {
		if (bookId == null) {
			throw new BookIdNullException();
		}
		if (authorId == null) {
			throw new AuthorIdNullException();
		}

		Author author = authorRepository.findById(authorId)
			.orElseThrow(() -> new AuthorNotFoundException(authorId));
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException());

		BookAuthor ba = bookAuthorRepository.findByBookAndAuthor(book, author)
			.orElseThrow(() -> new BookAuthorNotFoundException(bookId, authorId));

		bookAuthorRepository.delete(ba);
	}
}
