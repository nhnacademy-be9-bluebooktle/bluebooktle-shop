package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.frontend.repository.CategoryRepository;
import shop.bluebooktle.frontend.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	@Cacheable(value = "categoryTree")
	public List<CategoryTreeResponse> getCategoryTreeCached() {
		return categoryRepository.allCategoriesTree();
	}
}
