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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@Slf4j
public class PublisherController {

	private final PublisherService publisherService;

	@Operation(summary = "출판사 등록", description = "출판사를 등록합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> addPublisher(@Valid @RequestBody PublisherRequest request) {
		publisherService.registerPublisher(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "출판사 수정", description = "해당 출판사명을 수정합니다.")
	// @Auth(type = UserType.ADMIN)
	@PutMapping("/{publisher-id}")
	public ResponseEntity<JsendResponse<Void>> updatePublisher(
		@PathVariable(name = "publisher-id") Long publisherId,
		@Valid @RequestBody PublisherRequest request
	) {
		publisherService.updatePublisher(publisherId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "출판사 삭제", description = "해당 출판사를 삭제합니다.")
	@DeleteMapping("/{publisher-id}")
	public ResponseEntity<JsendResponse<Void>> deletePublisher(
		@PathVariable(name = "publisher-id") Long publisherId
	) {
		publisherService.deletePublisher(publisherId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "출판사 조회", description = "해당 출판사를 조회합니다.")
	@GetMapping("/{publisher-id}")
	public ResponseEntity<JsendResponse<PublisherInfoResponse>> getPublisher(
		@PathVariable(name = "publisher-id") Long publisherId
	) {
		PublisherInfoResponse response = publisherService.getPublisher(publisherId);
		return ResponseEntity.ok(JsendResponse.success(response));
	}
	
	@Operation(summary = "출판사 목록 조회", description = "등록된 출판사 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<PublisherInfoResponse>>> getPublishers(
		@PageableDefault(size = 10, sort = "id") Pageable pageable,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

		Page<PublisherInfoResponse> publisherPage;

		if (searchKeyword != null && !searchKeyword.isBlank()) {
			publisherPage = publisherService.searchPublishers(searchKeyword, pageable);
		} else {
			publisherPage = publisherService.getPublishers(pageable);
		}
		PaginationData<PublisherInfoResponse> paginationData = new PaginationData<>(publisherPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

}
