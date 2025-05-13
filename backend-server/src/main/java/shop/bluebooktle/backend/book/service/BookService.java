package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.request.BookRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.BookUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.BookAllResponse;
import shop.bluebooktle.backend.book.dto.response.BookRegisterResponse;
import shop.bluebooktle.backend.book.dto.response.BookResponse;
import shop.bluebooktle.backend.book.dto.response.BookUpdateResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.dto.response.BookResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.dto.response.BookResponse;
import shop.bluebooktle.backend.book.entity.Book;

public interface BookService {

	BookAllResponse findBookAllById(Long id);

	List<BookAllResponse> getBookAllByTitle(String title);

	boolean existsBookById(Long id);

	BookRegisterResponse registerBook(BookRegisterRequest request);

	Book getBookById(Long bookId);

	BookResponse findBookById(Long bookId);

	BookUpdateResponse updateBook(Long bookId, BookUpdateRequest request);

	void deleteBook(Long bookId);

}
