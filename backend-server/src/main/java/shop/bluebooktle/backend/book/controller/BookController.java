package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookRegisterRequest;
import shop.bluebooktle.backend.book.dto.response.BookResponse;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

	private final BookService bookService;
	private final BookRegisterService bookRegisterService;

	@GetMapping("/search/{bookId}")
	public ResponseEntity<JsendResponse<BookResponse>> getBook(@PathVariable Long bookId) {
		BookResponse bookResponse = bookService.findBookById(bookId);
		return ResponseEntity.ok(JsendResponse.success(bookResponse));
	}

	@GetMapping("/search/by-title")
	public ResponseEntity<JsendResponse<List<BookResponse>>> getBookByTitle(@RequestParam("title") String title) {
		List<BookResponse> bookResponses = bookService
			.getBookByTitle(title);
		return ResponseEntity.ok(JsendResponse.success(bookResponses));
	}

	@PostMapping("/register")
	public ResponseEntity<JsendResponse<Void>> registerBook(
		@Valid @RequestBody BookRegisterRequest request) {
		bookRegisterService.registerBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}
}
