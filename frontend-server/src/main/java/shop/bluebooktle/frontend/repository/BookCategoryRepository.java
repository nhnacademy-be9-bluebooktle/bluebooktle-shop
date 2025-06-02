package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(name = "backend-server", contextId = "bookCategoryRepository", path = "/api/categories", configuration = FeignGlobalConfig.class)
public interface BookCategoryRepository {

	@GetMapping("{categoryId}/books")
	PaginationData<BookInfoResponse> getBooksByCategory(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@PathVariable Long categoryId
	);

}
