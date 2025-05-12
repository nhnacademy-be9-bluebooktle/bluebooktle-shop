package shop.bluebooktle.backend.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.dto.request.PublisherRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.PublisherUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.PublisherInfoResponse;
import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("api/publishers")
@RequiredArgsConstructor
@Slf4j
public class PublisherController {

	private final PublisherService publisherService;

	// 출판사 등록
	@PostMapping
	public JsendResponse<Void> addPublisher(@RequestBody PublisherRegisterRequest request) {
		publisherService.addPublisher(request);
		return JsendResponse.success();
	}

	// 출판사명 수정
	@PutMapping("/{publisherId}")
	public JsendResponse<Void> updatePublisher(@PathVariable Long publisherId,
		@RequestBody PublisherUpdateRequest request) {
		publisherService.updatePublisher(request);
		return JsendResponse.success();
	}

	// 출판사 삭제
	@DeleteMapping("/{publisherId}")
	public JsendResponse<Void> deletePublisher(@PathVariable Long publisherId) {
		publisherService.deletePublisher(publisherId);
		return JsendResponse.success();
	}

	// 해당 출판사 조회
	@GetMapping("/{publisherId}")
	public JsendResponse<PublisherInfoResponse> getPublisher(@PathVariable Long publisherId) {
		return JsendResponse.success(publisherService.getPublisher(publisherId));
	}

	// 출판사 목록 조회
	@GetMapping
	public JsendResponse<Page<PublisherInfoResponse>> getPublishers(
		@PageableDefault(size = 10, sort = "id") Pageable pageable
	) {
		return JsendResponse.success(publisherService.getPublishers(pageable));
	}

}
