package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.CategoryRepository;
import shop.bluebooktle.frontend.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	public List<CategoryTreeResponse> searchAllCategoriesTree() {
		List<CategoryTreeResponse> response = categoryRepository.allCategoriesTree();
		return response;
	}

	@Override
	public Page<CategoryResponse> getCategories(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}

		PaginationData<CategoryResponse> response = categoryRepository.getPagedCategories(page, size,
			keyword);
		// PaginationData<CategoryResponse> data = response;
		List<CategoryResponse> categories = response.getContent();
		return new PageImpl<>(categories, pageable, response.getTotalElements());
	}

	@Override
	public CategoryResponse getCategory(Long id) {
		return categoryRepository.getCategory(id);
	}

	@Override
	public List<CategoryResponse> getAllCategories() {
		List<CategoryResponse> allCategories = categoryRepository.getAllCategories();
		return allCategories;
	}

	@Override
	public void addRootCategory(RootCategoryRegisterRequest request) {
		categoryRepository.addRootCategory(request);
	}

	@Override
	public void addCategory(Long parentCategoryId, CategoryRegisterRequest request) {
		categoryRepository.addCategory(parentCategoryId, request);
	}

	@Override
	public void updateCategory(Long categoryId, CategoryUpdateRequest request) {
		categoryRepository.updateCategory(categoryId, request);
	}

	@Override
	public void deleteCategory(Long parentCategoryId) {
		categoryRepository.deleteCategory(parentCategoryId);
	}
}
