package shop.bluebooktle.backend.book.controller;

import org.springframework.data.domain.Page;
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
import lombok.extern.slf4j.Slf4j;

import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateServiceRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

	private final BookService bookService;
	private final BookRegisterService bookRegisterService;

	//도서 등록
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerBook(
		@Valid @RequestBody BookAllRegisterRequest request) {
		bookRegisterService.registerBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	//도서 정보 조회
	@GetMapping("/{bookId}")
	public ResponseEntity<JsendResponse<BookDetailResponse>> getBook(@PathVariable Long bookId) {
		BookDetailResponse bookResponse = bookService.findBookById(bookId);
		return ResponseEntity.ok(JsendResponse.success(bookResponse));
	}

	//도서 정보 조회 - 관리자
	@GetMapping("/{bookId}/admin")
	public ResponseEntity<JsendResponse<BookAllResponse>> getBookByAdmin(@PathVariable Long bookId) {
		BookAllResponse bookResponse = bookService.findBookAllById(bookId);
		return ResponseEntity.ok(JsendResponse.success(bookResponse));
	}

	//도서 정보 수정
	@PutMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> updateBook(
		@PathVariable Long bookId,
		@Valid @RequestBody BookUpdateServiceRequest request) {
		bookService.updateBook(bookId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	//도서 정보 삭제
	@DeleteMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> deleteBook(@PathVariable Long bookId) {
		bookService.deleteBook(bookId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 도서 목록 조회 및 검색 기능
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<BookInfoResponse>>> getPagedBooks(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		Page<BookInfoResponse> responses = bookService.findAllBooks(page, size, searchKeyword);
		PaginationData<BookInfoResponse> paginationData = new PaginationData<>(responses);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	// 관리자 페이지 도서 목록 조회 및 검색 기능
	@GetMapping("/admin")
	public ResponseEntity<JsendResponse<PaginationData<AdminBookResponse>>> getPagedBooksByAdmin(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		Page<AdminBookResponse> responses = bookService.findAllBooksByAdmin(page, size, searchKeyword);
		PaginationData<AdminBookResponse> paginationData = new PaginationData<>(responses);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@GetMapping("/order/{bookId}")
	public ResponseEntity<JsendResponse<BookCartOrderResponse>> getBookCartOrders(
		@PathVariable Long bookId,
		@RequestParam("quantity") int quantity
	) {
		BookCartOrderResponse response = bookService.getBookCartOrder(bookId, quantity);
		return ResponseEntity.ok(JsendResponse.success(response));
	}
}
