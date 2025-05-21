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
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.FeignGlobalConfig;

@FeignClient(name = "backend-server", contextId = "categoryRepository", path = "/api/categories", configuration = FeignGlobalConfig.class)
public interface CategoryRepository {

	@GetMapping
	JsendResponse<PaginationData<CategoryResponse>> getPagedCategories(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	);

	@GetMapping("/tree")
	JsendResponse<List<CategoryTreeResponse>> allCategoriesTree();

	@GetMapping("/{categoryId}")
	JsendResponse<CategoryResponse> getCategory(
		@PathVariable Long categoryId
	);

	@GetMapping("/all")
	JsendResponse<List<CategoryResponse>> getAllCategories();

	@PostMapping
	JsendResponse<Void> addRootCategory(
		@RequestBody RootCategoryRegisterRequest request
	);

	@PostMapping("/{parentCategoryId}")
	JsendResponse<Void> addCategory(
		@PathVariable Long parentCategoryId,
		@RequestBody CategoryRegisterRequest categoryRegisterRequest
	);

	@PostMapping
	JsendResponse<Void> saveCategory(@RequestBody CategoryRegisterRequest categoryRegisterRequest);

	@PutMapping("/{categoryId}")
	JsendResponse<Void> updateCategory(
		@PathVariable("categoryId") Long categoryId,
		@RequestBody CategoryUpdateRequest categoryUpdateRequest
	);

	@DeleteMapping("/{categoryId}")
	JsendResponse<Void> deleteCategory(
		@PathVariable("categoryId") Long categoryId
	);

}
