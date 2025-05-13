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
import shop.bluebooktle.backend.book.dto.request.BookRegisterByAladinRequest;
import shop.bluebooktle.backend.book.dto.response.AladinBookResponseDto;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.backend.book.service.impl.AladinBookServiceImpl;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/books")
public class AladinBookController {

	private final AladinBookServiceImpl aladinBookService;
	private final BookRegisterService bookRegisterService;

	@GetMapping("/search")
	public ResponseEntity<JsendResponse<List<AladinBookResponseDto>>> searchBooks(@RequestParam String query) {
		List<AladinBookResponseDto> result = aladinBookService.searchBooks(query);
		return ResponseEntity.ok(JsendResponse.success(result));
	}

	@GetMapping("/select")
	public ResponseEntity<JsendResponse<AladinBookResponseDto>> selectBook(@RequestParam String isbn) {
		AladinBookResponseDto book = aladinBookService.getBookByIsbn(isbn);
		if (book == null) {
			throw new AladinBookNotFoundException("해당 ISBN에 대한 도서를 찾을 수 없습니다.");
		}
		return ResponseEntity.ok(JsendResponse.success(book));
	}

	@PostMapping("/register/aladin")
	public ResponseEntity<JsendResponse<Void>> registerAladinBook(
		@Valid @RequestBody BookRegisterByAladinRequest request) {
		bookRegisterService.registerBookByAladin(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}
}