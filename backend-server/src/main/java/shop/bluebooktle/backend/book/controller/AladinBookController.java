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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.AladinBookService;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aladin/books")
@Tag(name = "외부 API를 통한 도서 조회 및 도서 등록 API ", description = "알라딘 API를 사용한 조회 및 알라딘 도서 등록 API")
public class AladinBookController {

	private final AladinBookService aladinBookService;
	private final BookRegisterService bookRegisterService;

	@Operation(summary = "알라딘 API 도서 검색", description = "알라딘 API를 사용해 키워드에 해당하는 도서를 조회합니다.")
	@GetMapping("/aladin-search")
	public ResponseEntity<JsendResponse<List<AladinBookResponse>>> searchBooks(
		@RequestParam("keyword") String keyword,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		List<AladinBookResponse> result = aladinBookService.searchBooks(keyword, page, size);
		return ResponseEntity.ok(JsendResponse.success(result));
	}

	@Operation(summary = "알라딘 API 도서 등록", description = "알라딘 API를 사용해 해당 도서를 등록합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerAladinBook(
		@Valid @RequestBody BookAllRegisterByAladinRequest request) {
		bookRegisterService.registerBookByAladin(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}
}