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

	// 도서 카테고리 삭제
	void deleteBookCategory(Long bookId, Long categoryId);

	void updateBookCategory(Long bookId, List<Long> categoryIdList);

	// 해당 도서의 카테고리 수정
	void updateBookCategoryByBookCategoryId(Long updatedCategoryId, Long bookCategoryId);

	void updateBookCategory(Long updatedCategoryId, Long categoryId, Long bookId);

	// 특정 도서의 카테고리 목록 조회
	List<CategoryResponse> getCategoryByBookId(BookInfoRequest request);

	// 특정 카테고리 안에 등록된 도서 조회
	Page<BookInfoResponse> searchBooksByCategory(Long categoryId, Pageable pageable, BookSortType bookSortType);
}
