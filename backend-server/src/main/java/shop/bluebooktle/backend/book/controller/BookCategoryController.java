package shop.bluebooktle.backend.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.service.BookCategoryService;
import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/categories/{categoryId}/books")
@RequiredArgsConstructor
@Slf4j
public class BookCategoryController {

	private final BookCategoryService bookCategoryService;
	private final CategoryService categoryService;

	// 도서에 카테고리 추가
	@PostMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> addBookCategory(
		@PathVariable Long bookId,
		@PathVariable Long categoryId
	) {
		bookCategoryService.registerBookCategory(bookId, categoryId);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 도서에 해당 카테고리 삭제
	@DeleteMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> deleteBookCategory(
		@PathVariable Long bookId,
		@PathVariable Long categoryId
	) {
		bookCategoryService.deleteBookCategory(bookId, categoryId);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 해당 카테고리에 등록된 도서 목록 반환
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<BookInfoResponse>>> getBooksByCategory(
		@PathVariable Long categoryId,
		@PageableDefault(size = 10, sort = "id") Pageable pageable
	) {
		// TODO + title, image 등 보여줘야 함
		Page<BookInfoResponse> responses = bookCategoryService.searchBooksByCategory(categoryId, pageable);
		PaginationData<BookInfoResponse> paginationData = new PaginationData<>(responses);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	// TODO 해당 도서에 등록된 카테고리 목록 반환
	// 책의 세부적인 정보 반환에 가까우니까
}
