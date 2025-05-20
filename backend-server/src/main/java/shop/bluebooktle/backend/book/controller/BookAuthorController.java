package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.BookAuthorService;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RequiredArgsConstructor
@RestController
public class BookAuthorController {

	private final BookAuthorService bookAuthorService;

	// 도서에 작가 등록
	@PostMapping("/api/authors/{authorId}/books/{bookId}")
	public ResponseEntity<JsendResponse<Void>> addBookAuthor(
		@PathVariable Long authorId,
		@PathVariable Long bookId
	) {
		bookAuthorService.registerBookAuthor(authorId, bookId);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 작가의 모든 도서
	@GetMapping("/api/authors/{authorId}/books")
	public ResponseEntity<JsendResponse<List<BookInfoResponse>>> getBooksByAuthor(
		@PathVariable Long authorId
	) {
		List<BookInfoResponse> bookInfoResponses = bookAuthorService.getBookByAuthorId(authorId);
		return ResponseEntity.ok(JsendResponse.success(bookInfoResponses));
	}

	// 도서의 모든 작가
	@GetMapping("/api/books/{bookId}/authors")
	public ResponseEntity<JsendResponse<List<AuthorResponse>>> getAuthorsByBook(
		@PathVariable Long bookId
	) {
		List<AuthorResponse> authorResponses = bookAuthorService.getAuthorByBookId(bookId);
		return ResponseEntity.ok(JsendResponse.success(authorResponses));
	}

	// 도서에 작가 삭제
	@DeleteMapping("/api/authors/{authorId}/books/{bookId}")
	public ResponseEntity<JsendResponse<Void>> deleteBookAuthor(
		@PathVariable Long authorId,
		@PathVariable Long bookId
	) {
		bookAuthorService.deleteBookAuthor(authorId, bookId);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
