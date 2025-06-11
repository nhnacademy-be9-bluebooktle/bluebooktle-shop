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
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "관리자 페이지 태그 API", description = "관리자 태그 CRUD API")
public class TagController {

	private final TagService tagService;

	@Operation(summary = "태그 등록", description = "태그를 등록합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> addTag(@Valid @RequestBody TagRequest request) {
		tagService.registerTag(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "태그 수정", description = "해당 태그의 태그명을 수정합니다.")
	@PutMapping("/{tag-id}")
	public ResponseEntity<JsendResponse<Void>> updateTag(
		@PathVariable(name = "tag-id") Long tagId,
		@Valid @RequestBody TagRequest request
	) {
		tagService.updateTag(tagId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "태그 삭제", description = "해당 태그를 삭제합니다.")
	@DeleteMapping("/{tag-id}")
	public ResponseEntity<JsendResponse<Void>> deleteTag(
		@PathVariable(name = "tag-id") Long tagId
	) {
		tagService.deleteTag(tagId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "태그 조회", description = "해당 태그를 조회합니다.")
	@GetMapping("/{tag-id}")
	public ResponseEntity<JsendResponse<TagInfoResponse>> getTag(
		@PathVariable(name = "tag-id") Long tagId
	) {
		return ResponseEntity.ok(JsendResponse.success(tagService.getTag(tagId)));
	}

	@Operation(summary = "태그 목록 조회", description = "등록된 태그 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<TagInfoResponse>>> getTags(
		@PageableDefault(size = 10, sort = "id") Pageable pageable,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

		Page<TagInfoResponse> tagPage;

		if (searchKeyword != null && !searchKeyword.isBlank()) {
			tagPage = tagService.searchTags(searchKeyword, pageable);
		} else {
			tagPage = tagService.getTags(pageable);
		}
		PaginationData<TagInfoResponse> paginationData = new PaginationData<>(tagPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}
}
