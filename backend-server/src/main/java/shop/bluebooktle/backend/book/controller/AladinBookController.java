package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.AladinBookService;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aladin/books")
public class AladinBookController {

	private final AladinBookService aladinBookService;
	private final BookRegisterService bookRegisterService;

	// 알라딘 API 도서 검색
	@GetMapping("/aladin-search")
	public ResponseEntity<JsendResponse<List<AladinBookResponse>>> searchBooks(
		@RequestParam("keyword") String keyword,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		List<AladinBookResponse> result = aladinBookService.searchBooks(keyword, page, size);
		return ResponseEntity.ok(JsendResponse.success(result));
	}

	// isbn으로 도서정보 가져오기
	@GetMapping("/select")
	public ResponseEntity<JsendResponse<AladinBookResponse>> selectBook(@RequestParam String isbn) {
		AladinBookResponse book = aladinBookService.getBookByIsbn(isbn);
		if (book == null) {
			throw new AladinBookNotFoundException("해당 ISBN에 대한 도서를 찾을 수 없습니다.");
		}
		return ResponseEntity.ok(JsendResponse.success(book));
	}

	// 알라딘 api로 도서 등록
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerAladinBook(
		@Valid @RequestBody BookAllRegisterByAladinRequest request) {
		bookRegisterService.registerBookByAladin(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}
}