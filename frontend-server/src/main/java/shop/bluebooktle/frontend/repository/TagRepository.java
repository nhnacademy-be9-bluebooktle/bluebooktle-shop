package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.FeignGlobalConfig;

/** 백엔드의 /api/tags REST 엔드포인트를 호출하는 HTTP 클라이언트 */
@FeignClient(name = "backend-server", contextId = "tagRepository", path = "/api/tags", configuration = FeignGlobalConfig.class)
public interface TagRepository {
	// 태그 전체 조회
	@GetMapping
	JsendResponse<PaginationData<TagInfoResponse>> getTags(
		@RequestParam("page") int page,    // 페이지 번호
		@RequestParam("size") int size,    // 페이지 당 아이템 수
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword // 검색 키워드 (옵션)
	);

	// 단일 태그 조회
	@GetMapping("/{tagId}")
	JsendResponse<TagInfoResponse> getTag(@PathVariable("tagId") Long id);

	// 태그 등록
	@PostMapping
	JsendResponse<Void> createTag(@RequestBody TagRequest request);

	// 태그 수정 (태그명 수정)
	@PutMapping("/{tagId}")
	JsendResponse<Void> updateTag(
		@PathVariable("tagId") Long id,
		@RequestBody TagRequest request);

	// 태그 삭제
	@DeleteMapping("/{tagId}")
	JsendResponse<Void> deleteTag(@PathVariable("tagId") Long id);
}
