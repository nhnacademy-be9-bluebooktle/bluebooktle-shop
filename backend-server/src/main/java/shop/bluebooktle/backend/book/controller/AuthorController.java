package shop.bluebooktle.backend.book.controller;

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
import shop.bluebooktle.backend.book.dto.request.author.AuthorRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.author.AuthorUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.author.AuthorResponse;
import shop.bluebooktle.backend.book.service.AuthorService;
import shop.bluebooktle.common.dto.common.JsendResponse;

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
	@GetMapping("/{id}")
	public ResponseEntity<JsendResponse<AuthorResponse>> getAuthor(
		@PathVariable Long id
	) {
		AuthorResponse authorResponse = authorService.getAuthor(id);
		return ResponseEntity.ok(JsendResponse.success(authorResponse));
	}

	// 작가 수정
	@PutMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> updateAuthor(
		@PathVariable Long id,
		@Valid @RequestBody AuthorUpdateRequest authorUpdateRequest
	) {
		authorService.updateAuthor(id, authorUpdateRequest);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 작가 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> deleteAuthor(
		@PathVariable Long id
	) {
		authorService.deleteAuthor(id);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
