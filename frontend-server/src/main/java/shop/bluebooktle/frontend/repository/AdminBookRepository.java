package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateServiceRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", name = "adminBookRepository", path = "/api/books", configuration = FeignGlobalConfig.class)
public interface AdminBookRepository {

	@PostMapping
	void registerBook(@RequestBody BookAllRegisterRequest bookAllRegisterRequest);

	@GetMapping("/admin")
	PaginationData<AdminBookResponse> getPagedBooksByAdmin(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	);

	@GetMapping("/{bookId}/admin")
	BookAllResponse getBook(@PathVariable("bookId") Long bookId);

	@PutMapping("/{bookId}")
	void updateBook(
		@PathVariable Long bookId,
		@RequestBody BookUpdateServiceRequest bookUpdateServiceRequest
	);

	@DeleteMapping("/{bookId}")
	void deleteBook(@PathVariable("bookId") Long bookId);

	@GetMapping("/order/{bookId}")
	BookCartOrderResponse getBookCartOrder(@PathVariable("bookId") Long bookId, @RequestParam int quantity);

	@GetMapping("/aladin-search")
	List<AladinBookResponse> searchAladinBooks(
		@RequestParam("keyword") String keyword,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	);
}
