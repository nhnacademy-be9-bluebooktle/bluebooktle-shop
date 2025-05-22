package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.common.dto.book.request.BookRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateRequest;
import shop.bluebooktle.common.dto.book.response.BookRegisterResponse;
import shop.bluebooktle.common.dto.book.response.BookResponse;
import shop.bluebooktle.common.dto.book.response.BookUpdateResponse;

public interface BookService {

	BookRegisterResponse registerBook(BookRegisterRequest request);

	BookResponse findBookById(Long bookId);

	BookUpdateResponse updateBook(Long bookId, BookUpdateRequest request);

	void deleteBook(Long bookId);

	Book getBookById(Long bookId);

	BookAllResponse findBookAllById(Long id);

	List<BookAllResponse> getBookAllByTitle(String title);

	boolean existsBookById(Long id);

}
