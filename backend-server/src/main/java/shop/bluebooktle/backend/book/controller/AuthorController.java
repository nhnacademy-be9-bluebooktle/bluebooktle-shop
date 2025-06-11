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
import shop.bluebooktle.backend.book.service.AuthorService;
import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "관리자 페이지 작가 API", description = "관리자 작가 CRUD API")
public class AuthorController {

	private final AuthorService authorService;

	@Operation(summary = "작가 등록", description = "새로운 작가를 등록합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerAuthor(
		@Valid @RequestBody AuthorRegisterRequest authorRegisterRequest
	) {
		authorService.registerAuthor(authorRegisterRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "작가 조회", description = "해당 작가를 조회합니다.")
	@GetMapping("/{authorId}")
	public ResponseEntity<JsendResponse<AuthorResponse>> getAuthor(
		@PathVariable Long authorId
	) {
		AuthorResponse authorResponse = authorService.getAuthor(authorId);
		return ResponseEntity.ok(JsendResponse.success(authorResponse));
	}

	@Operation(summary = "작가 수정", description = "해당 작가의 작가명을 수정합니다.")
	@PutMapping("/{authorId}")
	public ResponseEntity<JsendResponse<Void>> updateAuthor(
		@PathVariable Long authorId,
		@Valid @RequestBody AuthorUpdateRequest authorUpdateRequest
	) {
		authorService.updateAuthor(authorId, authorUpdateRequest);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 작가 삭제
	@Operation(summary = "작가 삭제", description = "해당 작가를 삭제합니다.")
	@DeleteMapping("/{authorId}")
	public ResponseEntity<JsendResponse<Void>> deleteAuthor(
		@PathVariable Long authorId
	) {
		authorService.deleteAuthor(authorId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "작가 목록 조회", description = "등록된 작가의 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<AuthorResponse>>> getPagedAuthors(
		@PageableDefault(size = 10, sort = "id") Pageable pageable,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		Page<AuthorResponse> authorPage;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			authorPage = authorService.searchAuthors(searchKeyword, pageable);
		} else {
			authorPage = authorService.getAuthors(pageable);
		}
		PaginationData<AuthorResponse> paginationData = new PaginationData<>(authorPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}
}
