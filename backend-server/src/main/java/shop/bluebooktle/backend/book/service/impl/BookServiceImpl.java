package shop.bluebooktle.backend.book.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.common.exception.BookAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@Service

@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	BookRepository bookRepository;

	@Transactional
	@Override
	public void registerBook(Book book) {
		if (existsBookById(book.getId())) {
			throw new BookAlreadyExistsException();
		}
		bookRepository.save(book);
	}

	@Transactional(readOnly = true)
	@Override
	public Book getBookById(Long id) {
		if (!existsBookById(id)) {
			throw new BookNotFoundException(String.format("Book not found (id : %d)", id));
		}
		return bookRepository.findById(id).get();
	}

	@Override
	public List<Book> getBookByTitle(String title) {
		return List.of();
	}

	@Override
	public boolean existsBookById(Long id) {
		return bookRepository.existsById(id);
	}
}
