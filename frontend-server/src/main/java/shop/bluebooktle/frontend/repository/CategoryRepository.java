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

import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", name = "categoryRepository", path = "/api/categories", configuration = FeignGlobalConfig.class)
public interface CategoryRepository {

	@GetMapping
	PaginationData<CategoryResponse> getPagedCategories(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	);

	@GetMapping("/tree")
	List<CategoryTreeResponse> allCategoriesTree();

	@PostMapping
	@RetryWithTokenRefresh
	Void addRootCategory(
		@RequestBody RootCategoryRegisterRequest request
	);

	@PostMapping("/{parentCategoryId}")
	@RetryWithTokenRefresh
	void addCategory(
		@PathVariable Long parentCategoryId,
		@RequestBody CategoryRegisterRequest categoryRegisterRequest
	);

	@GetMapping("/{categoryId}")
	CategoryResponse getCategory(
		@PathVariable Long categoryId
	);

	@PutMapping("/{categoryId}")
	@RetryWithTokenRefresh
	void updateCategory(
		@PathVariable("categoryId") Long categoryId,
		@RequestBody CategoryUpdateRequest categoryUpdateRequest
	);

	@DeleteMapping("/{categoryId}")
	@RetryWithTokenRefresh
	void deleteCategory(
		@PathVariable("categoryId") Long categoryId
	);

	@GetMapping("/name/{categoryName}")
	CategoryResponse getCategoryByName(@PathVariable String categoryName);

}
