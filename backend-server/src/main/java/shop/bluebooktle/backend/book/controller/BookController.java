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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateServiceRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "도서 API", description = "관리자 도서 CRUD 및 도서 조회 API")
public class BookController {

	private final BookService bookService;
	private final BookRegisterService bookRegisterService;

	@Operation(summary = "도서 직접 등록", description = "사용자에게 입력받은 값을 사용해 도서를 등록합니다.")
	@PostMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> registerBook(
		@Valid @RequestBody BookAllRegisterRequest request) {
		bookRegisterService.registerBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "도서 조회 - 상세 페이지", description = "도서 상세 페이지를 위해 해당 도서의 상세 정보를 조회합니다.")
	@GetMapping("/{book-id}")
	public ResponseEntity<JsendResponse<BookDetailResponse>> getBook(
		@PathVariable(name = "book-id") Long bookId
	) {
		BookDetailResponse bookResponse = bookService.findBookById(bookId);
		return ResponseEntity.ok(JsendResponse.success(bookResponse));
	}

	@Operation(summary = "도서 조회 - 관리자", description = "관리자 페이지의 도서 수정 페이지를 위해 해당 도서의 상세 정보를 조회합니다.")
	@GetMapping("/{book-id}/admin")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<BookAllResponse>> getBookByAdmin(
		@PathVariable(name = "book-id") Long bookId
	) {
		BookAllResponse bookResponse = bookService.findBookAllById(bookId);
		return ResponseEntity.ok(JsendResponse.success(bookResponse));
	}

	@Operation(summary = "도서 수정", description = "해당 도서의 정보를 수정합니다.")
	@PutMapping("/{book-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> updateBook(
		@PathVariable(name = "book-id") Long bookId,
		@Valid @RequestBody BookUpdateServiceRequest request
	) {
		bookService.updateBook(bookId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "도서 삭제", description = "해당 도서를 삭제합니다.")
	@DeleteMapping("/{book-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> deleteBook(
		@PathVariable(name = "book-id") Long bookId
	) {
		bookService.deleteBook(bookId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(
		summary = "키워드 검색 및 정렬 결과 도서 목록 조회",
		description = "키워드 검색 및 정렬한 도서 목록을 조회합니다."
	)
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<BookInfoResponse>>> getPagedBooks(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@RequestParam("bookSortType") BookSortType bookSortType
	) {
		Page<BookInfoResponse> responses = bookService.findAllBooks(page, size, searchKeyword, bookSortType);
		PaginationData<BookInfoResponse> paginationData = new PaginationData<>(responses);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(
		summary = "도서 목록 조회 및 검색 기능 - 관리자",
		description = "관리자 페이지에서 등록된 도서 목록 및 키워드 검색 결과 도서 목록을 조회합니다."
	)
	@GetMapping("/admin")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PaginationData<AdminBookResponse>>> getPagedBooksByAdmin(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		Page<AdminBookResponse> responses = bookService.findAllBooksByAdmin(page, size, searchKeyword);
		PaginationData<AdminBookResponse> paginationData = new PaginationData<>(responses);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@GetMapping("/order/{book-id}")
	public ResponseEntity<JsendResponse<BookCartOrderResponse>> getBookCartOrders(
		@PathVariable(name = "book-id") Long bookId,
		@RequestParam("quantity") int quantity
	) {
		BookCartOrderResponse response = bookService.getBookCartOrder(bookId, quantity);
		return ResponseEntity.ok(JsendResponse.success(response));
	}
}
