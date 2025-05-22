package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.FeignGlobalConfig;

@FeignClient(name = "backend-server", contextId = "adminPublisherRepository", path = "/api/publishers", configuration = FeignGlobalConfig.class)
public interface AdminPublisherRepository {
	// 출판사 조회
	@GetMapping
	JsendResponse<PaginationData<PublisherInfoResponse>> getPublishers(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	);

	// 출판사ID로 단일 조회
	@GetMapping("/{publisherId}")
	JsendResponse<PublisherInfoResponse> getPublisher(@PathVariable("publisherId") Long id);

	// 출판사 등록
	@PostMapping
	JsendResponse<Void> createPublisher(@RequestParam PublisherRequest request);

	// 출판사명 수정
	@PutMapping("/{publisherId}")
	JsendResponse<Void> updatePublisher(
		@PathVariable("publisherId") Long id,
		@RequestParam PublisherRequest request
	);

	// 출판사 삭제
	@GetMapping("/{publisherId}")
	JsendResponse<Void> deletePublisher(@PathVariable("publisherId") Long id);
}

