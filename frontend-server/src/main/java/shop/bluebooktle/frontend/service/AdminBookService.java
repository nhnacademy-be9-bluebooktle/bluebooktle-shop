package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;

public interface AdminBookService {
	BookAllResponse getBook(Long bookId);

	void registerBook(BookAllRegisterRequest bookAllRegisterRequest);

	Page<BookAllResponse> getPagedBooks(int page, int size, String searchKeyword);

	void deleteBook(Long bookId);
}
