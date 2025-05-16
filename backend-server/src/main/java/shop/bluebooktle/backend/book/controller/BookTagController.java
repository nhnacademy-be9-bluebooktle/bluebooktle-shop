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
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.service.BookTagService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tags/{tagId}/books")
public class BookTagController {
	private final BookTagService bookTagService;

	// 해당 도서에 태그 등록
	@PostMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> addBookTag(
		@PathVariable Long tagId,
		@PathVariable Long bookId
	) {
		bookTagService.registerBookTag(tagId, bookId);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 해당 도서에 태그 삭제
	@DeleteMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> deleteBookTag(
		@PathVariable Long tagId,
		@PathVariable Long bookId
	) {
		bookTagService.deleteBookTag(tagId, bookId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 특정 태그에 해당되는 도서
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<BookInfoResponse>>> getBooksByTag(
		@PathVariable Long tagId,
		@PageableDefault(size = 10, sort = "id") Pageable pageable
	) {
		// TODO BookInfoRespnse : title, imageUrl, 작가, 출판사..? 등등 들어가야할듯
		Page<BookInfoResponse> bookPage = bookTagService.searchBooksByTag(tagId, pageable);
		PaginationData<BookInfoResponse> paginationData = new PaginationData<>(bookPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));

	}
}
