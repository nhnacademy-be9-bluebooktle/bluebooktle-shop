package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "카테고리 API", description = "관리자 카테고리 CRUD 및 카테고리 조회 API")
public class CategoryController {

	private final CategoryService categoryService;

	@Operation(summary = "카테고리 목록 조회", description = "등록된 카테고리 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<CategoryResponse>>> getCategories(
		@PageableDefault(size = 10, sort = "id") Pageable pageable,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		Page<CategoryResponse> categoryPage;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			categoryPage = categoryService.searchCategories(searchKeyword, pageable);
		} else {
			categoryPage = categoryService.getCategories(pageable);
		}
		PaginationData<CategoryResponse> paginationData = new PaginationData<>(categoryPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(
		summary = "카테고리 트리 구조 조회",
		description = "최상위 카테고리부터 시작해 모든 하위 카테고리를 재귀적으로 포함한 트리 구조로 반환합니다."
	)
	@GetMapping("/tree")
	public ResponseEntity<JsendResponse<List<CategoryTreeResponse>>> getCategoryTree() {
		List<CategoryTreeResponse> tree = categoryService.getCategoryTree();
		return ResponseEntity.ok(JsendResponse.success(tree));
	}

	@Operation(
		summary = "최상위 카테고리 등록",
		description = "최소 2단계의 카테고리 구조를 위해 최상위 카테고리와 하위 카테고리를 등록합니다."
	)
	@PostMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> addRootCategory(
		@Valid @RequestBody RootCategoryRegisterRequest request) {
		categoryService.registerRootCategory(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "중간 카테고리 등록", description = "해당 카테고리 하위의 카테고리를 등록합니다.")
	@PostMapping("/{parent-category-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> addCategory(
		@PathVariable(name = "parent-category-id") Long parentCategoryId,
		@Valid @RequestBody CategoryRegisterRequest request
	) {
		categoryService.registerCategory(parentCategoryId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "카테고리 조회", description = "해당 카테고리를 조회합니다.")
	@GetMapping("/{category-id}")
	public ResponseEntity<JsendResponse<CategoryResponse>> getCategory(
		@PathVariable(name = "category-id") Long categoryId
	) {
		CategoryResponse categoryResponse = categoryService.getCategory(categoryId);
		return ResponseEntity.ok(JsendResponse.success(categoryResponse));
	}

	@Operation(summary = "카테고리 삭제", description = "해당 카테고리를 삭제합니다.")
	@DeleteMapping("/{category-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> deleteCategory(
		@PathVariable(name = "category-id") Long categoryId
	) {
		categoryService.deleteCategory(categoryId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "카테고리 수정", description = "해당 카테고리명을 수정합니다.")
	@PutMapping("/{category-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> updateCategory(
		@PathVariable(name = "category-id") Long categoryId,
		@Valid @RequestBody CategoryUpdateRequest request
	) {
		categoryService.updateCategory(categoryId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "최상위 카테고리 이름으로 조회", description = "최상위 카테고리를 이름으로 조회합니다.")
	@GetMapping("/name/{category-name}")
	public ResponseEntity<JsendResponse<CategoryResponse>> getCategoryByName(
		@PathVariable(name = "category-name") String categoryName
	) {
		CategoryResponse categoryResponse = categoryService.getCategoryByName(categoryName);
		return ResponseEntity.ok(JsendResponse.success(categoryResponse));
	}

}
