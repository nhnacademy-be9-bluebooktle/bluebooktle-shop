package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;

public interface BookService {
	BookCartOrderResponse getBookCartOrder(Long bookId, int quantity);
}
