package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.FeignGlobalConfig;

@FeignClient(name = "backend-server", contextId = "authorRepository", path = "/api/authors", configuration = FeignGlobalConfig.class)
public interface AuthorRepository {
	@GetMapping
	PaginationData<AuthorResponse> getPagedAuthors(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	);

	@GetMapping("/{authorId}")
	AuthorResponse getAuthor(
		@PathVariable Long authorId
	);

	@PostMapping
	void addAuthor(
		@RequestBody AuthorRegisterRequest request
	);

	@PutMapping("/{authorId}")
	void updateAuthor(
		@PathVariable("authorId") Long authorId,
		@RequestBody AuthorUpdateRequest AuthorUpdateRequest
	);

	@DeleteMapping("/{authorId}")
	void deleteAuthor(
		@PathVariable("authorId") Long authorId
	);

}
