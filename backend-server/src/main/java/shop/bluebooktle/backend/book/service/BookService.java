package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.response.BookResponse;
import shop.bluebooktle.backend.book.entity.Book;

public interface BookService {

	BookResponse findBookById(Long id);

	List<BookResponse> getBookByTitle(String title);

	boolean existsBookById(Long id);

	Book getBookById(Long bookId);
}
