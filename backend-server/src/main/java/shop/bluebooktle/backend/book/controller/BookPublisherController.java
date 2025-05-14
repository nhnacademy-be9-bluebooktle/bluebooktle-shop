package shop.bluebooktle.backend.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.service.BookPublisherService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/publishers/{publisherId}/books")
@RequiredArgsConstructor
@Slf4j
public class BookPublisherController {
	private final BookPublisherService bookPublisherService;

	// 도서에 출판사 등록
	@PostMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> addBookPublisher(
		@PathVariable Long publisherId,
		@PathVariable Long bookId
	) {
		bookPublisherService.registerBookPublisher(bookId, publisherId);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 해당 도서의 출판사 삭제
	@DeleteMapping("/{bookId}")
	public ResponseEntity<JsendResponse<Void>> deleteBookPublisher(
		@PathVariable Long publisherId,
		@PathVariable Long bookId
	) {
		bookPublisherService.deleteBookPublisher(bookId, publisherId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 특정 출판사 안에 등록된 도서 조회
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<BookInfoResponse>>> getBooksByPublisher(
		@PathVariable Long publisherId,
		@PageableDefault(size = 10, sort = "id") Pageable pageable
	) {
		// TODO BookInfoRespnse : title, imageUrl, 작가, 출판사..? 등등 들어가야할듯
		Page<BookInfoResponse> bookPage = bookPublisherService.searchBooksByPublisher(publisherId, pageable);
		PaginationData<BookInfoResponse> paginationData = new PaginationData<>(bookPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	// TODO 특정 도서의 출판사 목록 조회 -> 도서 상세보기에 들어가야 할 듯

}
