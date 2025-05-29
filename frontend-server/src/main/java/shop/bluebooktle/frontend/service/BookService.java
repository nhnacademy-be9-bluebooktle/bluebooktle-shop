package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.response.BookAllResponse;

public interface BookService {
	BookCartOrderResponse getBookCartOrder(Long bookId, int quantity);

	Page<BookAllResponse> getPagedBooks(int page, int size, String searchKeyword);
}
