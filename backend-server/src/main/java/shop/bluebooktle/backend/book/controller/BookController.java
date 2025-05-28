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
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.service.AladinBookService;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookRegisterResponse;
import shop.bluebooktle.common.dto.book.response.BookResponse;
import shop.bluebooktle.common.dto.book.response.BookUpdateResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

	private final BookService bookService;
	private final BookRegisterService bookRegisterService;
	private final AladinBookService aladinBookService;

	//도서 정보 등록
	@PostMapping("/only-book-table")
	public ResponseEntity<JsendResponse<BookRegisterResponse>> registerBook(
		@Valid @RequestBody BookRegisterRequest request) {
		BookRegisterResponse response = bookService.registerBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(response));
	}

	//도서 등록
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerBook(
		@Valid @RequestBody BookAllRegisterRequest request) {
		bookRegisterService.registerBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
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

	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<BookAllResponse>>> getPagedBooks(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		PaginationData<BookAllResponse> data = bookService.findAllBooks(page, size, searchKeyword);
		return ResponseEntity.ok(JsendResponse.success(data));
	}

	// 알라딘 API 도서 검색
	@GetMapping("/aladin-search")
	public ResponseEntity<JsendResponse<List<AladinBookResponse>>> searchBooks(
		@RequestParam("keyword") String keyword,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		log.info("[API] Aladin search keyword={}, page={}, size={}", keyword, page, size);
		List<AladinBookResponse> result = aladinBookService.searchBooks(keyword, page, size);
		return ResponseEntity.ok(JsendResponse.success(result));
	}

	// TODO 관리자페이지 먼저 하고나서 수정
	// @GetMapping("cart/{bookId}")
	// public ResponseEntity<JsendResponse<List<BookCartOrderResponse>>> getBookCartOrders(@PathVariable Long bookId) {
	// 	List<BookCartOrderResponse> bookCartOrderResponse = bookService.getBookCartOrders(bookId);
	// 	return ResponseEntity.ok(JsendResponse.success(bookCartOrderResponse));
	// }

	// //메인페이지에 표시될 정보(id, title, author, price, salePrice, imgUrl) 조회
	// @GetMapping("/main/{bookId}")
	// public ResponseEntity<JsendResponse<Page<BookInfoResponse>>> getBooksForMainPage(
	// 	@PathVariable Long bookId,
	// 	@PageableDefault(size = 10) Pageable pageable) {
	//
	// 	Page<BookInfoResponse> bookMainResponses = bookService
	// 		.getBooksForMainPage(bookId, pageable);
	// 	return ResponseEntity.ok(JsendResponse.success(bookMainResponses));
	// }
	//
	// //제목으로 검색하여 표시될 정보(id, title, author, price, salePrice, imgUrl) 조회
	// @GetMapping("/search")
	// public ResponseEntity<JsendResponse<Page<BookInfoResponse>>> searchBooks(
	// 	@RequestParam("title") String title,
	// 	@PageableDefault(size = 10) Pageable pageable) {
	// 	Page<BookInfoResponse> books = bookService.searchBooksByTitle(title, pageable);
	// 	return ResponseEntity.ok(JsendResponse.success(books));
	// }
}
