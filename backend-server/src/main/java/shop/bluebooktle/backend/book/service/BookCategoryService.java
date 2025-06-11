package shop.bluebooktle.backend.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.request.BookInfoRequest;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;

public interface BookCategoryService {

	// 도서 카테고리 등록 -> 도서당 최대 10개 카테고리 가짐
	void registerBookCategory(Long bookId, Long categoryId);

	void registerBookCategory(Long bookId, List<Long> categoryIdList);

	void updateBookCategory(Long bookId, List<Long> categoryIdList);

	// 특정 카테고리 안에 등록된 도서 조회
	Page<BookInfoResponse> searchBooksByCategory(Long categoryId, Pageable pageable, BookSortType bookSortType);
}
