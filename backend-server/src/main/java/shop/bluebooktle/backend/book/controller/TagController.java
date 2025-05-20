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
import shop.bluebooktle.backend.book.service.TagService;
import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

	private final TagService tagService;

	// 태그 등록
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> addTag(@Valid @RequestBody TagRequest request) {
		tagService.registerTag(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 태그 수정 (태그명 수정)
	@PutMapping("/{tagId}")
	public ResponseEntity<JsendResponse<Void>> updateTag(
		@PathVariable Long tagId,
		@Valid @RequestBody TagRequest request
	) {
		tagService.updateTag(tagId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 태그 삭제
	@DeleteMapping("/{tagId}")
	public ResponseEntity<JsendResponse<Void>> deleteTag(@PathVariable Long tagId) {
		tagService.deleteTag(tagId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 태그 조회
	@GetMapping("/{tagId}")
	public ResponseEntity<JsendResponse<TagInfoResponse>> getTag(@PathVariable Long tagId) {
		return ResponseEntity.ok(JsendResponse.success(tagService.getTag(tagId)));
	}

	// 태그 목록 조회
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<TagInfoResponse>>> getTags(
		@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		Page<TagInfoResponse> tagPage = tagService.getTags(pageable);
		PaginationData<TagInfoResponse> paginationData = new PaginationData<>(tagPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

}
