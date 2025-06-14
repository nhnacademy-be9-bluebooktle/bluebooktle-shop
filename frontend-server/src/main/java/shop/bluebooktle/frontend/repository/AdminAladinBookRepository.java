package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", name = "adminAladinBookRepository", path = "/api/aladin/books", configuration = FeignGlobalConfig.class)
public interface AdminAladinBookRepository {

	@GetMapping("/aladin-search")
	@RetryWithTokenRefresh
	List<AladinBookResponse> searchBooks(
		@RequestParam("keyword") String keyword,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	);

	@PostMapping
	@RetryWithTokenRefresh
	void registerAladinBook(BookAllRegisterByAladinRequest request);
}
