package shop.bluebooktle.backend.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.dto.request.CategoryRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.CategoryUpdateRequest;
import shop.bluebooktle.backend.book.dto.request.RootCategoryRegisterRequest;
import shop.bluebooktle.backend.book.dto.response.CategoryResponse;
import shop.bluebooktle.backend.book.dto.response.CategoryTreeResponse;

public interface CategoryService {

	void registerCategory(Long parentCategoryId, CategoryRegisterRequest request);

	void registerRootCategory(RootCategoryRegisterRequest request);

	void updateCategory(Long categoryId, CategoryUpdateRequest request);

	void deleteCategory(Long categoryId);

	CategoryResponse getCategory(Long categoryId);

	boolean isRootCategory(Long id);

	// 상위 카테고리에 포함되는 하위 카테고리 목록을 가져옴
	List<CategoryResponse> getSubcategoriesByParentCategoryId(Long parentCategoryId);

	// 하위 카테고리가 포함되는 상위 카테고리 목록을 가져옴
	List<CategoryResponse> getParentCategoriesByLeafCategoryId(Long leafCategoryId);

	Page<CategoryResponse> getCategories(Pageable pageable);

	// 최상위 카테고리부터 시작해서 모든 자식 카테고리를 재귀적으로 포함한 트리 구조 조회
	List<CategoryTreeResponse> getCategoryTree();

	CategoryTreeResponse getCategoryTreeById(Long categoryId);

}
