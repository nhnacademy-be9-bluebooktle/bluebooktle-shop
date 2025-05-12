package shop.bluebooktle.backend.book.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.CategoryRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.CategoryUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.CategoryResponse;
import shop.bluebooktle.backend.book.dto.response.CategoryTreeResponse;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.common.exception.book.CategoryAlreadyExistsException;
import shop.bluebooktle.common.exception.book.CategoryCannotDeleteRootException;
import shop.bluebooktle.common.exception.book.CategoryNotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;

	@Override
	@Transactional
	public void registerCategory(CategoryRegisterRequest request) {
		if (categoryRepository.existsByName(request.name())) {
			throw new CategoryAlreadyExistsException("이미 존재하는 카테고리명입니다. 카테고리명: " + request.name());
		}

		Category parent = null;
		if (request.parentCategoryId() != null) {
			parent = categoryRepository.findById(request.parentCategoryId())
				.orElseThrow(() -> new CategoryNotFoundException(request.parentCategoryId()));
		}

		// 등록할 카테고리 엔티티 생성
		Category newCategory = Category.builder()
			.name(request.name())
			.parentCategory(parent)
			.build();

		// 최상위 카테고리인 경우 기본 하위 카테고리 등록
		if (parent == null) {
			Category defaultChild = new Category(newCategory, newCategory.getName() + "- 기본 하위 카테고리");
			newCategory.addChildCategory(defaultChild);
		}

		categoryRepository.save(newCategory);
	}

	@Override
	@Transactional
	public void updateCategory(CategoryUpdateRequest request) {
		if (!categoryRepository.existsById(request.id())) {
			throw new CategoryNotFoundException(request.id());
		}
		if (categoryRepository.existsByNameAndIdNot(request.name(), request.id())) {
			throw new CategoryAlreadyExistsException("Category name already exists: " + request.name());
		}
		Category category = categoryRepository.findById(request.id()).get();
		category.setName(request.name());
		categoryRepository.save(category);
	}

	@Override
	@Transactional
	public void deleteCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));

		// 최상위 카테고리는 삭제 불가
		if (isRootCategory(categoryId)) {
			throw new CategoryCannotDeleteRootException();
		}
		// 하위 모든 카테고리 수집
		List<Category> descendants = getAllDescendantCategories(category);
		// 연관된 BookCategory 삭제
		bookCategoryRepository.deleteByCategoryIn(descendants); // 손자 카테고리까지 삭제
		// 하위 모든 카테고리 삭제
		categoryRepository.deleteAll(descendants);

		// 현재 카테고리의 BookCategory 관계 삭제
		bookCategoryRepository.deleteByCategory(category);

		// 카테고리 삭제
		categoryRepository.delete(category);

	}

	@Override
	@Transactional(readOnly = true)
	public boolean isRootCategory(Long id) {
		Category selectedCategory = categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotFoundException(id));
		return categoryRepository.existsByIdAndParentCategoryIsNull(selectedCategory.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public CategoryResponse getCategory(Long id) {
		Category selectedCategory = categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotFoundException(id));
		CategoryResponse response = new CategoryResponse(selectedCategory.getId(), selectedCategory.getName());
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> getSubcategoriesByParentCategoryId(Long parentCategoryId) {
		Category parentCategory = categoryRepository.findById(parentCategoryId)
			.orElseThrow(() -> new CategoryNotFoundException(parentCategoryId));
		List<Category> categories = parentCategory.getChildCategories();
		List<CategoryResponse> subcategories = new ArrayList<>();
		for (Category category : categories) {
			CategoryResponse response = new CategoryResponse(category.getId(), category.getName());
			subcategories.add(response);
		}
		return subcategories;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> getParentCategoriesByLeafCategoryId(Long leafCategoryId) {
		List<Category> parents = new ArrayList<>();
		List<CategoryResponse> parentcategories = new ArrayList<>();
		Category current = categoryRepository.findById(leafCategoryId)
			.orElseThrow(() -> new CategoryNotFoundException(leafCategoryId));

		while (current.getParentCategory() != null) {
			current = categoryRepository.findParentCategoryById(current.getId());
			parents.add(current);
		}
		for (Category category : parents) {
			CategoryResponse response = new CategoryResponse(category.getId(), category.getName());
			parentcategories.add(response);
		}
		return parentcategories;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CategoryResponse> getCategories(Pageable pageable) {
		Page<Category> categoryPage = categoryRepository.findAll(pageable);

		return categoryPage.map(c ->
			new CategoryResponse(c.getId(), c.getName())
		);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryTreeResponse> getCategoryTree() {
		List<Category> roots = categoryRepository.findByParentCategoryIsNull();
		return roots.stream()
			.map(this::toTreeDto)
			.collect(Collectors.toList());
	}

	@Override
	public CategoryTreeResponse getCategoryTreeById(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		return toTreeDto(category);
	}

	private CategoryTreeResponse toTreeDto(Category category) {
		CategoryTreeResponse response = new CategoryTreeResponse(category.getId(), category.getName());
		for (Category child : category.getChildCategories()) {
			response.children().add(toTreeDto(child));
		}
		return response;
	}

	@Transactional(readOnly = true)
	protected List<Category> getAllDescendantCategories(Category parent) {
		List<Category> result = new ArrayList<>();
		collectDescendants(parent, result);
		return result;
	}

	@Transactional(readOnly = true)
	protected void collectDescendants(Category category, List<Category> result) {
		for (Category child : category.getChildCategories()) {
			result.add(child);
			collectDescendants(child, result); // 재귀 호출로 카테고리에 있는 모든 하위 카테고리를 추가
		}
	}

}
