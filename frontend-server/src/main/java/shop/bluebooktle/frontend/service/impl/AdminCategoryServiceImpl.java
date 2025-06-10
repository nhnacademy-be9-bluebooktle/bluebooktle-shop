package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
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
import shop.bluebooktle.frontend.service.AdminCategoryService;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	public List<CategoryTreeResponse> getCategoryTree() {
		List<CategoryTreeResponse> response = categoryRepository.allCategoriesTree();
		return response;
	}

	@Override
	public Page<CategoryResponse> getCategories(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);
		PaginationData<CategoryResponse> response = categoryRepository.getPagedCategories(page, size,
			searchKeyword);
		List<CategoryResponse> categories = response.getContent();
		return new PageImpl<>(categories, pageable, response.getTotalElements());
	}

	@Override
	public CategoryResponse getCategory(Long id) {
		return categoryRepository.getCategory(id);
	}

	@Override
	@CacheEvict(value = "categoryTree", allEntries = true)
	public void addRootCategory(RootCategoryRegisterRequest request) {
		categoryRepository.addRootCategory(request);
	}

	@Override
	@CacheEvict(value = "categoryTree", allEntries = true)
	public void addCategory(Long parentCategoryId, CategoryRegisterRequest request) {
		categoryRepository.addCategory(parentCategoryId, request);
	}

	@Override
	@CacheEvict(value = "categoryTree", allEntries = true)
	public void updateCategory(Long categoryId, CategoryUpdateRequest request) {
		categoryRepository.updateCategory(categoryId, request);
	}

	@Override
	@CacheEvict(value = "categoryTree", allEntries = true)
	public void deleteCategory(Long parentCategoryId) {
		categoryRepository.deleteCategory(parentCategoryId);
	}
}
