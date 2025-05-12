package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.dto.request.BookCategoryRequest;
import shop.bluebooktle.backend.book.dto.request.CategoryInfoRequest;
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.service.BookCategoryService;
import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api/categories/{categoryId}")
@RequiredArgsConstructor
@Slf4j
public class BookCategoryController {

	private final BookCategoryService bookCategoryService;
	private final CategoryService categoryService;

	// 도서에 카테고리 추가
	@PostMapping("/books/{bookId}")
	public JsendResponse<Void> addCategory(
		@PathVariable Long bookId,
		@PathVariable Long categoryId
	) {
		BookCategoryRequest request = new BookCategoryRequest(bookId, categoryId);
		bookCategoryService.registerBookCategory(request);
		return JsendResponse.success();
	}

	// 도서에 해당 카테고리 삭제
	@DeleteMapping("/books/{bookId}")
	public JsendResponse<Void> deleteCategory(
		@PathVariable Long bookId,
		@PathVariable Long categoryId
	) {
		BookCategoryRequest request = new BookCategoryRequest(bookId, categoryId);
		bookCategoryService.deleteBookCategory(request);
		return JsendResponse.success();
	}

	// 해당 카테고리에 등록된 도서 목록 반환
	@GetMapping("/books")
	public JsendResponse<List<BookInfoResponse>> getBooksByCategory(
		@PathVariable Long categoryId
	) {
		CategoryInfoRequest request = new CategoryInfoRequest(categoryId);
		List<BookInfoResponse> responses = bookCategoryService.searchBooksByCategory(request);
		return JsendResponse.success(responses);
	}

	// TODO 해당 도서에 등록된 카테고리 목록 반환
	// 책의 세부적인 정보 반환에 가까우니까
}
