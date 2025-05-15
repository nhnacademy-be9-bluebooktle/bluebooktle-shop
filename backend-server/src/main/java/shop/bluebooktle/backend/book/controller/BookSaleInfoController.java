package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoResponse;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.service.BookSaleInfoService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-sale-info")
public class BookSaleInfoController {

	// 아직수정필요

	private final BookSaleInfoService bookSaleInfoService;

	@GetMapping("/{id}")
	public ResponseEntity<JsendResponse<BookSaleInfoResponse>> getBookSaleInfoById(@PathVariable Long id) {
		BookSaleInfo bookSaleInfo = bookSaleInfoService.findById(id);
		return ResponseEntity.ok(JsendResponse.success(BookSaleInfoResponse.fromEntity(bookSaleInfo)));
	}

	@GetMapping
	public ResponseEntity<JsendResponse<List<BookSaleInfoResponse>>> getAllBookSaleInfos() {
		List<BookSaleInfo> bookSaleInfos = bookSaleInfoService.findAll();
		List<BookSaleInfoResponse> responses = bookSaleInfos.stream()
			.map(BookSaleInfoResponse::fromEntity)
			.toList();
		return ResponseEntity.ok(JsendResponse.success(responses));
	}

	@PutMapping("/{id}")
	public ResponseEntity<JsendResponse<BookSaleInfoResponse>> updateBookSaleInfo(
		@PathVariable Long id,
		@RequestBody BookSaleInfoUpdateRequest request
	) {
		BookSaleInfo updatedEntity = bookSaleInfoService.update(id, request);
		return ResponseEntity.ok(JsendResponse.success(BookSaleInfoResponse.fromEntity(updatedEntity)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> deleteBookSaleInfo(@PathVariable Long id) {
		bookSaleInfoService.deleteById(id);
		return ResponseEntity.ok(JsendResponse.success(null));
	}

}