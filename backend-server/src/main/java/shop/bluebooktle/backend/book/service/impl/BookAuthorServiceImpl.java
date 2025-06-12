package shop.bluebooktle.backend.book.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.service.BookAuthorService;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.exception.book.AuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookAuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookAuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class BookAuthorServiceImpl implements BookAuthorService {

	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;
	private final BookAuthorRepository bookAuthorRepository;

	@Override
	public AuthorResponse registerBookAuthor(Long bookId, Long authorId) {

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
		return AuthorResponse.builder().
			id(author.getId()).
			name(author.getName()).
			createdAt(author.getCreatedAt()).
			build();
	}

	@Override
	public List<AuthorResponse> registerBookAuthor(Long bookId, List<Long> authorIdList) {
		List<AuthorResponse> responses = new ArrayList<>();
		for (Long authorId : authorIdList) {
			AuthorResponse response = registerBookAuthor(bookId, authorId);
			responses.add(response);
		}
		return responses;
	}

	@Override
	public List<AuthorResponse> updateBookAuthor(Long bookId, List<Long> authorIdList) {
		// 도서에 등록되었던 기존 작가 삭제
		List<BookAuthor> bookAuthorList = bookAuthorRepository.findByBookId(bookId);
		bookAuthorRepository.deleteAll(bookAuthorList);
		// 다시 새롭게 등록
		return registerBookAuthor(bookId, authorIdList);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AuthorResponse> getAuthorByBookId(Long bookId) {

		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException());

		return bookAuthorRepository.findByBookId(book.getId()).stream()
			.map(bookAuthor -> AuthorResponse.builder()
				.id(bookAuthor.getAuthor().getId())
				.name(bookAuthor.getAuthor().getName())
				.createdAt(bookAuthor.getAuthor().getCreatedAt())
				.build())
			.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookInfoResponse> getBookByAuthorId(Long authorId) {

		Author author = authorRepository.findById(authorId)
			.orElseThrow(() -> new AuthorNotFoundException(authorId));

		/*return bookAuthorRepository.findBooksByAuthor(author).stream()
			.map(b -> new BookInfoResponse(b.getId()))
			.toList();*/
		return null;
	}

	@Override
	public void deleteBookAuthor(Long bookId, Long authorId) {

		Author author = authorRepository.findById(authorId)
			.orElseThrow(() -> new AuthorNotFoundException(authorId));
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException());

		BookAuthor ba = bookAuthorRepository.findByBookAndAuthor(book, author)
			.orElseThrow(() -> new BookAuthorNotFoundException(bookId, authorId));

		bookAuthorRepository.delete(ba);
	}
}
