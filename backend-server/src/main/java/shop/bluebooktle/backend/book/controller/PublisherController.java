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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.dto.request.PublisherRequest;
import shop.bluebooktle.backend.book.dto.response.PublisherInfoResponse;
import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@Slf4j
public class PublisherController {

	private final PublisherService publisherService;

	// 출판사 등록
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> addPublisher(@Valid @RequestBody PublisherRequest request) {
		publisherService.registerPublisher(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 출판사명 수정
	@PutMapping("/{publisherId}")
	public ResponseEntity<JsendResponse<Void>> updatePublisher(
		@PathVariable Long publisherId,
		@Valid @RequestBody PublisherRequest request
	) {
		publisherService.updatePublisher(publisherId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 출판사 삭제
	@DeleteMapping("/{publisherId}")
	public ResponseEntity<JsendResponse<Void>> deletePublisher(@PathVariable Long publisherId) {
		publisherService.deletePublisher(publisherId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 해당 출판사 조회
	@GetMapping("/{publisherId}")
	public ResponseEntity<JsendResponse<PublisherInfoResponse>> getPublisher(@PathVariable Long publisherId) {
		PublisherInfoResponse response = publisherService.getPublisher(publisherId);
		return ResponseEntity.ok(JsendResponse.success(response));
	}

	// 출판사 목록 조회
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<PublisherInfoResponse>>> getPublishers(
		@PageableDefault(size = 10, sort = "id") Pageable pageable
	) {
		Page<PublisherInfoResponse> publisherPage = publisherService.getPublishers(pageable);
		PaginationData<PublisherInfoResponse> paginationData = new PaginationData<>(publisherPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

}
