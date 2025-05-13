package shop.bluebooktle.backend.book.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public JsendResponse<Void> registerAuthor(
		@RequestBody AuthorRegisterRequest authorRegisterRequest
	) {
		authorService.registerAuthor(authorRegisterRequest);
		return JsendResponse.success();
	}

	// 작가 조회
	@GetMapping("/{id}")
	public JsendResponse<AuthorResponse> getAuthor(
		@PathVariable Long id
	) {
		AuthorResponse authorResponse = authorService.getAuthor(id);
		return JsendResponse.success(authorResponse);
	}

	// 작가 수정
	@PutMapping("/{id}")
	public JsendResponse<Void> updateAuthor(
		@PathVariable Long id,
		@RequestBody AuthorUpdateRequest authorUpdateRequest
	) {
		authorService.updateAuthor(id, authorUpdateRequest);
		return JsendResponse.success();
	}

	// 작가 삭제
	@DeleteMapping("/{id}")
	public JsendResponse<Void> deleteAuthor(
		@PathVariable Long id
	) {
		authorService.deleteAuthor(id);
		return JsendResponse.success();
	}
}
