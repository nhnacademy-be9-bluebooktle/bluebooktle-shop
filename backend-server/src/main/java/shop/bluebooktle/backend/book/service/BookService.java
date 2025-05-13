package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.response.BookResponse;

public interface BookService {

	BookResponse findBookById(Long id);

	List<BookResponse> getBookByTitle(String title);

	boolean existsBookById(Long id);
}
