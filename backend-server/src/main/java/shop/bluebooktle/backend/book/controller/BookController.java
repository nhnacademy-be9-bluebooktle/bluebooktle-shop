package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookAllRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.BookRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.BookUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.BookAllResponse;
import shop.bluebooktle.backend.book.dto.response.BookRegisterResponse;
import shop.bluebooktle.backend.book.dto.response.BookResponse;
import shop.bluebooktle.backend.book.dto.response.BookUpdateResponse;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

	private final BookService bookService;
	private final BookRegisterService bookRegisterService;

	//도서 정보 등록
	@PostMapping
	public ResponseEntity<JsendResponse<BookRegisterResponse>> registerBook(
		@Valid @RequestBody BookRegisterRequest request) {
		BookRegisterResponse response = bookService.registerBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(response));
	}

	//도서 정보 조회
	@GetMapping("/{bookId}")
	public ResponseEntity<JsendResponse<BookResponse>> getBook(@PathVariable Long bookId) {
		BookResponse bookResponse = bookService.findBookById(bookId);
		return ResponseEntity.ok(JsendResponse.success(bookResponse));
	}

	//도서 정보 수정
	@PutMapping("/{bookId}")
	public ResponseEntity<JsendResponse<BookUpdateResponse>> updateBook(
		@PathVariable Long bookId,
		@Valid @RequestBody BookUpdateRequest request) {

		BookUpdateResponse response = bookService.updateBook(bookId, request);
		return ResponseEntity.ok(JsendResponse.success(response));
	}

	//도서 정보 삭제
	@DeleteMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> deleteBook(@PathVariable Long bookId) {
		bookService.deleteBook(bookId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	//해당 도서 관련된 모든 정보(도서,도서 판매정보,작가,출판사,태그,이미지,카테고리)까지 등록
	@PostMapping("/all")
	public ResponseEntity<JsendResponse<Void>> registerBookAll(
		@Valid @RequestBody BookAllRegisterRequest request) {
		bookRegisterService.registerBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	//해당 도서 관련된 모든 정보(도서,도서 판매정보,작가,출판사,태그,이미지,카테고리)까지 bookId로 조회
	@GetMapping("/all/{bookId}")
	public ResponseEntity<JsendResponse<BookAllResponse>> getBookAll(@PathVariable Long bookId) {
		BookAllResponse bookAllResponse = bookService.findBookAllById(bookId);
		return ResponseEntity.ok(JsendResponse.success(bookAllResponse));
	}

	//해당 도서 관련된 모든 정보(도서,도서 판매정보,작가,출판사,태그,이미지,카테고리)까지 제목으로 조회
	@GetMapping("/all/by-title")
	public ResponseEntity<JsendResponse<List<BookAllResponse>>> getBookAllByTitle(@RequestParam("title") String title) {
		List<BookAllResponse> bookAllRespons = bookService
			.getBookAllByTitle(title);
		return ResponseEntity.ok(JsendResponse.success(bookAllRespons));
	}
}
