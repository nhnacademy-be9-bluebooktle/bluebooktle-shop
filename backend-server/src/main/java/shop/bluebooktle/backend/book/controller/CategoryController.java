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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.dto.request.CategoryRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.CategoryUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.CategoryResponse;
import shop.bluebooktle.backend.book.dto.response.CategoryTreeResponse;
import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

	private final CategoryService categoryService;

	// 모든 카테고리 조회 (단계적 구조로 출력 x)
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<CategoryResponse>>> getCategories(
		@PageableDefault(size = 10, sort = "id") Pageable pageable) {

		Page<CategoryResponse> categoryPage = categoryService.getCategories(pageable);
		PaginationData<CategoryResponse> paginationData = new PaginationData<>(categoryPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	// 최상위 카테고리부터 시작해서 모든 자식 카테고리를 재귀적으로 포함한 트리 구조 조회
	@GetMapping("/tree")
	public ResponseEntity<JsendResponse<List<CategoryTreeResponse>>> getCategoryTree() {
		List<CategoryTreeResponse> tree = categoryService.getCategoryTree();
		return ResponseEntity.ok(JsendResponse.success(tree));
	}

	// 해당 카테고리 포함한 모든 자식 카테고리를 재귀적으로 포함한 트리 구조 조회
	@GetMapping("/{categoryId}/tree")
	public ResponseEntity<JsendResponse<CategoryTreeResponse>> getCategoryTree(@PathVariable Long categoryId) {
		CategoryTreeResponse tree = categoryService.getCategoryTreeById(categoryId);
		return ResponseEntity.ok(JsendResponse.success(tree));
	}

	// 카테고리 등록
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> addCategory(@Valid @RequestBody CategoryRegisterRequest request) {
		categoryService.registerCategory(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 해당 카테고리 조회
	@GetMapping("/{categoryId}")
	public ResponseEntity<JsendResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId) {
		CategoryResponse categoryResponse = categoryService.getCategory(categoryId);
		return ResponseEntity.ok(JsendResponse.success(categoryResponse));
	}

	// 카테고리 삭제
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<JsendResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 카테고리명 수정
	@PutMapping("/{categoryId}")
	public ResponseEntity<JsendResponse<Void>> updateCategory(
		@PathVariable Long categoryId,
		@Valid @RequestBody CategoryUpdateRequest request
	) {

		categoryService.updateCategory(categoryId, request);
		// TODO 업데이트면 return ResponseEntity.noContent().build(); 이렇게 해야할지.. 응답코드 : 204
		return ResponseEntity.ok(JsendResponse.success());
	}

	// 상위 카테고리에 포함되는 하위 카테고리 목록을 가져옴
	@GetMapping("/{categoryId}/subcategories")
	public ResponseEntity<JsendResponse<List<CategoryResponse>>> getSubcategories(@PathVariable Long categoryId) {
		List<CategoryResponse> subs = categoryService.getSubcategoriesByParentCategoryId(categoryId);
		return ResponseEntity.ok(JsendResponse.success(subs));
	}

	// 하위 카테고리가 포함되는 상위 카테고리 목록을 가져옴
	@GetMapping("/{categoryId}/parentcategories")
	public ResponseEntity<JsendResponse<List<CategoryResponse>>> getParentCategories(@PathVariable Long categoryId) {
		List<CategoryResponse> parents = categoryService.getParentCategoriesByLeafCategoryId(categoryId);
		return ResponseEntity.ok(JsendResponse.success(parents));
	}

}
