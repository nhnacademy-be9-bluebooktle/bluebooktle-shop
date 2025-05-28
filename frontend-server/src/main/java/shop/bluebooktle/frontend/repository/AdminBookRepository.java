package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", name = "BookRepository", path = "/api/books", configuration = FeignGlobalConfig.class)
public interface AdminBookRepository {

	@GetMapping
	PaginationData<BookAllResponse> getPagedBooks(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	);

	@GetMapping("/{bookId}")
	BookAllResponse getBook(@PathVariable("bookId") Long bookId);

	@PostMapping()
	void registerBook(@RequestBody BookAllRegisterRequest bookAllRegisterRequest);

	@DeleteMapping("/{bookId}")
	void deleteBook(@PathVariable("bookId") Long bookId);
}
