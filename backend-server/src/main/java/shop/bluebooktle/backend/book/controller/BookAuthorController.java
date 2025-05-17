package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.service.BookAuthorService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/authors/{authorId}/books")
public class BookAuthorController {
	private final BookAuthorService bookAuthorService;

	// 도서에 작가 등록
	@PostMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> addBookAuthor(
		@PathVariable Long authorId,
		@PathVariable Long bookId
	) {
		bookAuthorService.registerBookAuthor(authorId, bookId);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 도서에 작가 삭제
	@DeleteMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> deleteBookAuthor(
		@PathVariable Long authorId,
		@PathVariable Long bookId
	) {
		bookAuthorService.deleteBookAuthor(authorId, bookId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 작가의 모든 도서
	@GetMapping
	public ResponseEntity<JsendResponse<List<BookInfoResponse>>> getBookAuthors(
		@PathVariable Long authorId
	) {
		List<BookInfoResponse> response = bookAuthorService.getBookByAuthorId(authorId);
		return ResponseEntity.ok(JsendResponse.success(response));
	}
}
