package shop.bluebooktle.backend.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class AuthorController {

	private final AuthorService authorService;

	// 작가 생성
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerAuthor(
		@Valid @RequestBody AuthorRegisterRequest authorRegisterRequest
	) {
		authorService.registerAuthor(authorRegisterRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 작가 조회
	@GetMapping("/{authorId}")
	public ResponseEntity<JsendResponse<AuthorResponse>> getAuthor(
		@PathVariable Long authorId
	) {
		AuthorResponse authorResponse = authorService.getAuthor(authorId);
		return ResponseEntity.ok(JsendResponse.success(authorResponse));
	}

	// 작가 수정
	@PatchMapping("/{authorId}")
	public ResponseEntity<JsendResponse<Void>> updateAuthor(
		@PathVariable Long authorId,
		@Valid @RequestBody AuthorUpdateRequest authorUpdateRequest
	) {
		authorService.updateAuthor(authorId, authorUpdateRequest);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 작가 삭제
	@DeleteMapping("/{authorId}")
	public ResponseEntity<JsendResponse<Void>> deleteAuthor(
		@PathVariable Long authorId
	) {
		authorService.deleteAuthor(authorId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 작가 목록 조회
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
