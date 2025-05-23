package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.request.BookCategoryRequest;
import shop.bluebooktle.backend.book.dto.request.BookInfoRequest;
import shop.bluebooktle.backend.book.dto.request.CategoryInfoRequest;
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.dto.response.CategoryResponse;

public interface BookCategoryService {

	// 도서 카테고리 등록 -> 도서당 최대 10개 카테고리 가짐
	void registerBookCategory(BookCategoryRequest request);

	// 도서 카테고리 삭제
	void deleteBookCategory(BookCategoryRequest request);

	// 특정 도서의 카테고리 목록 조회
	List<CategoryResponse> getCategoryByBookId(BookInfoRequest request);

	// 특정 카테고리 안에 등록된 도서 조회
	List<BookInfoResponse> searchBooksByCategory(CategoryInfoRequest request);
}
