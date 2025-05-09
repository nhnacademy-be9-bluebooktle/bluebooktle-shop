package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.request.CategoryRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.CategoryUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.CategoryResponse;

public interface CategoryService {

	void registerCategory(CategoryRegisterRequest request);

	void updateCategory(CategoryUpdateRequest request);

	void deleteCategory(Long id);

	CategoryResponse getCategory(Long id);

	boolean isRootCategory(Long id);

	// 상위 카테고리에 포함되는 하위 카테고리들을 가져옴
	List<CategoryResponse> getSubcategoriesByParentCategoryId(Long parentCategoryId);

	// 하위 카테고리에 해당하는 상위 카테고리 목록을 가져옴
	List<CategoryResponse> getParentCategoriesByLeafCategoryId(Long leafCategoryId);

}
