package shop.bluebooktle.backend.book.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.request.BookUpdateServiceRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;

public interface BookService {

	BookDetailResponse findBookById(Long bookId);

	void updateBook(Long bookId, BookUpdateServiceRequest request);

	void deleteBook(Long bookId);

	BookAllResponse findBookAllById(Long bookId);

	Page<BookInfoResponse> findAllBooks(int page, int size, String searchKeyword, BookSortType bookSortType);

	Page<AdminBookResponse> findAllBooksByAdmin(int page, int size, String searchKeyword);

	BookCartOrderResponse getBookCartOrder(Long bookId, int quantity);
}
