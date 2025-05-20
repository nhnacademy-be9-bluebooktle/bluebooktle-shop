package shop.bluebooktle.backend.book.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.service.BookSaleInfoService;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoResponse;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoUpdateResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-sale-infos")
public class BookSaleInfoController {

	private final BookSaleInfoService bookSaleInfoService;

	//도서판매정보 등록
	@PostMapping
	public ResponseEntity<JsendResponse<BookSaleInfoRegisterResponse>> registerBookSaleInfo(
		@Valid @RequestBody BookSaleInfoRegisterRequest request) {
		BookSaleInfoRegisterResponse response = bookSaleInfoService.save(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(response));
	}

	//도서판매정보 조회
	@GetMapping("/{id}")
	public ResponseEntity<JsendResponse<BookSaleInfoUpdateResponse>> getBookSaleInfoById(@PathVariable Long id) {
		BookSaleInfo bookSaleInfo = bookSaleInfoService.findById(id);
		return ResponseEntity.ok(JsendResponse.success(BookSaleInfoUpdateResponse.fromEntity(bookSaleInfo)));
	}

	//도서판매정보 수정
	@PutMapping("/{id}")
	public ResponseEntity<JsendResponse<BookSaleInfoUpdateResponse>> updateBookSaleInfo(
		@PathVariable Long id,
		@Valid @RequestBody BookSaleInfoUpdateRequest request
	) {
		BookSaleInfoUpdateResponse response = bookSaleInfoService.update(id, request);
		return ResponseEntity.ok(JsendResponse.success(response));
	}

	//도서판매정보 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> deleteBookSaleInfo(@PathVariable Long id) {
		bookSaleInfoService.deleteById(id);
		return ResponseEntity.ok(JsendResponse.success());
	}

}