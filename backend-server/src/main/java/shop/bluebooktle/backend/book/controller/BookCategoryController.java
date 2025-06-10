package shop.bluebooktle.backend.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.service.BookCategoryService;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/categories/{category-id}/books")
@RequiredArgsConstructor
@Slf4j
public class BookCategoryController {

	private final BookCategoryService bookCategoryService;

	@Operation(summary = "카테고리 도서 목록 반환", description = "해당 카테고리 하위에 등록된 도서들을 반환합니다.")
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<BookInfoResponse>>> getBooksByCategory(
		@PathVariable(name = "category-id") Long categoryId,
		@PageableDefault(size = 10, sort = "id") Pageable pageable,
		@RequestParam("bookSortType") BookSortType bookSortType
	) {
		Page<BookInfoResponse> responses = bookCategoryService.searchBooksByCategory(categoryId, pageable,
			bookSortType);
		PaginationData<BookInfoResponse> paginationData = new PaginationData<>(responses);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}
}
