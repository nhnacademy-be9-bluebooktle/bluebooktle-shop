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

@FeignClient(url = "${server.gateway-url}", name = "CategoryRepository", path = "/api/categories", configuration = FeignGlobalConfig.class)
public interface CategoryRepository {

	@GetMapping
	PaginationData<CategoryResponse> getPagedCategories(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	);

	@GetMapping("/tree")
	List<CategoryTreeResponse> allCategoriesTree();

	@GetMapping("/{categoryId}")
	CategoryResponse getCategory(
		@PathVariable Long categoryId
	);

	@PostMapping
	Void addRootCategory(
		@RequestBody RootCategoryRegisterRequest request
	);

	@PostMapping("/{parentCategoryId}")
	void addCategory(
		@PathVariable Long parentCategoryId,
		@RequestBody CategoryRegisterRequest categoryRegisterRequest
	);

	@PostMapping
	void saveCategory(@RequestBody CategoryRegisterRequest categoryRegisterRequest);

	@PutMapping("/{categoryId}")
	void updateCategory(
		@PathVariable("categoryId") Long categoryId,
		@RequestBody CategoryUpdateRequest categoryUpdateRequest
	);

	@DeleteMapping("/{categoryId}")
	void deleteCategory(
		@PathVariable("categoryId") Long categoryId
	);

}
