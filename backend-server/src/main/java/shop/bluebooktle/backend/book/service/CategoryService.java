package shop.bluebooktle.backend.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;

public interface CategoryService {

	void registerCategory(Long parentCategoryId, CategoryRegisterRequest request);

	void registerRootCategory(RootCategoryRegisterRequest request);

	void updateCategory(Long categoryId, CategoryUpdateRequest request);

	void deleteCategory(Long categoryId);

	CategoryResponse getCategory(Long categoryId);

	Page<CategoryResponse> getCategories(Pageable pageable);

	// 최상위 카테고리부터 시작해서 모든 자식 카테고리를 재귀적으로 포함한 트리 구조 조회
	List<CategoryTreeResponse> getCategoryTree();

	Page<CategoryResponse> searchCategories(String searchKeyword, Pageable pageable);

	CategoryResponse getCategoryByName(String categoryName);
}
