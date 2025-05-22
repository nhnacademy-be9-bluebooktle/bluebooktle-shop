package shop.bluebooktle.frontend.service;

import java.util.List;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;

public interface AdminCategoryService {

	// 전체 카테고리 트리 조회
	List<CategoryTreeResponse> searchAllCategoriesTree();

	// 카테고리 리스트 조회
	Page<CategoryResponse> getCategories(int page, int size, String searchKeyword);

	// 카테고리 조회
	CategoryResponse getCategory(Long id);

	// 카테고리 생성
	void addRootCategory(RootCategoryRegisterRequest request);

	void addCategory(Long parentCategoryId, CategoryRegisterRequest request);

	// 카테고리 수정
	void updateCategory(Long parentCategoryId, CategoryUpdateRequest request);

	// 카테고리 삭제
	void deleteCategory(Long parentCategoryId);
}
