package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;

public interface BookService {
	BookCartOrderResponse getBookCartOrder(Long bookId, int quantity);

	Page<BookInfoResponse> getPagedBooks(int page, int size, String searchKeyword);

	Page<BookInfoResponse> getPagedBooksByCategoryId(int page, int size, Long categoryId);

	CategoryResponse getCategoryById(Long categoryId);

	// 도서 조회
	BookDetailResponse getBookDetail(Long bookId);
}
