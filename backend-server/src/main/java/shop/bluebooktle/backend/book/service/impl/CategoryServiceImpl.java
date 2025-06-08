package shop.bluebooktle.backend.book.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.exception.book.CategoryAlreadyExistsException;
import shop.bluebooktle.common.exception.book.CategoryCannotDeleteException;
import shop.bluebooktle.common.exception.book.CategoryCannotDeleteRootException;
import shop.bluebooktle.common.exception.book.CategoryNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;

	@Override
	public void registerCategory(Long parentCategoryId, CategoryRegisterRequest request) {
		// 부모 카테고리의 하위카테고리 중 중복된 이름으로 등록 불가능
		// 부모 카테고리의 하위 카테고리들의 이름들을 가져와야 함 -> HOW? getAllDescendantCategories(Category parent) 일단 불러서 하위 카테고리들 다 가ㅕㅈ오기
		Category parent = categoryRepository.findById(parentCategoryId)
			.orElseThrow(() -> new CategoryNotFoundException(parentCategoryId));

		StringBuilder categoryPathBuilder = new StringBuilder();

		List<Category> categoryList = getAllDescendantCategories(parent);
		categoryList.stream().map(Category::getName).forEach(categoryName -> {
			if (categoryName.equals(request.name())) {
				throw new CategoryAlreadyExistsException("이미 존재하는 카테고리명입니다. 카테고리명: " + request.name());
			}
		});

		// 등록할 카테고리 엔티티 생성
		Category newCategory = Category.builder()
			.name(request.name())
			.parentCategory(parent)
			.build();

		newCategory = categoryRepository.save(newCategory);

		// 카테고리 경로 등록하기
		List<CategoryResponse> categoryResponseList = getParentCategoriesByLeafCategoryId(parent.getId());
		categoryResponseList = categoryResponseList.reversed();
		categoryPathBuilder.append("/");
		for (CategoryResponse categoryResponse : categoryResponseList) {
			categoryPathBuilder.append(categoryResponse.categoryId()).append("/");
		}

		// 카테고리 경로 (자신의 categoryId도 포함)
		categoryPathBuilder.append(newCategory.getId());
		String categoryPathStr = categoryPathBuilder.toString();

		newCategory.setCategoryPath(categoryPathStr);
		categoryRepository.save(newCategory);

		parent.addChildCategory(newCategory);
		categoryRepository.save(parent);
	}

	@Override
	public void registerRootCategory(RootCategoryRegisterRequest request) {
		List<Category> rootCategoryList = categoryRepository.findByParentCategoryIsNull();
		rootCategoryList.stream().map(Category::getName).forEach(categoryName -> {
			if (request.getRootCategoryName().equals(categoryName)) {
				throw new CategoryAlreadyExistsException("이미 존재하는 카테고리명입니다. 카테고리명: " + request.getRootCategoryName());
			}
		});
		Category rootCategory = Category.builder()
			.name(request.getRootCategoryName())
			.parentCategory(null)
			.build();

		Category childCategory = Category.builder()
			.name(request.getChildCategoryName())
			.parentCategory(rootCategory)
			.build();
		rootCategory = categoryRepository.save(rootCategory);
		childCategory = categoryRepository.save(childCategory);

		rootCategory.setCategoryPath("/" + rootCategory.getId().toString());
		childCategory.setCategoryPath("/" + rootCategory.getId().toString() + "/" + childCategory.getId().toString());

	}

	@Override
	public void updateCategory(Long categoryId, CategoryUpdateRequest request) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		if (category.getParentCategory() != null) {
			List<Category> categoryList = getAllDescendantCategories(category.getParentCategory());
			categoryList.stream().map(Category::getName).forEach(categoryName -> {
				if (categoryName.equals(request.name())) {
					throw new CategoryAlreadyExistsException("이미 존재하는 카테고리명입니다. 카테고리명: " + request.name());
				}
			});
		}

		category.setName(request.name());
		categoryRepository.save(category);
	}

	@Override
	public void deleteCategory(Long categoryId) {
		log.info("삭제 요청 : categoryId: {}", categoryId);
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));

		// TODO 카테고리에 도서 등록시 삭제 불가(하위 카테고리 포함)
		if (bookCategoryRepository.existsByCategory(category)) {
			throw new CategoryCannotDeleteRootException("(도서가 등록된 카테고리 삭제 불가)");
		}
		// 하위 모든 카테고리 수집
		List<Category> descendants = getAllDescendantCategories(category);
		for (Category descendant : descendants) {
			if (bookCategoryRepository.existsByCategory(descendant)) {
				throw new CategoryCannotDeleteRootException("(도서가 등록된 하위 카테고리 존재시 삭제 불가)");
			}
		}

		// 최상위 카테고리는 삭제 불가
		if (isRootCategory(categoryId)) {
			throw new CategoryCannotDeleteRootException("(최상위 카테고리 삭제 불가)");
		}
		// 상위 카테고리가 최상위 카테고리이면서 2단계 카테고리가 1개일 경우 삭제 불가능
		if (isRootCategory(category.getParentCategory().getId())) {
			Category rootCategory = categoryRepository.findById(category.getParentCategory().getId())
				.orElseThrow(() -> new CategoryNotFoundException(category.getParentCategory().getId())); // 최상위 카테고리
			if (rootCategory.getChildCategories().size() == 1) {
				throw new CategoryCannotDeleteException("(카테고리는 최소 2단계 카테고리 유지)");
			}
		}

		// 연관된 BookCategory 삭제
		bookCategoryRepository.deleteByCategoryIn(descendants); // 손자 카테고리까지 삭제
		// 하위 모든 카테고리 삭제
		for (Category childCategory : descendants) {
			childCategory.setParentCategory(null);
			log.info("자식 카테고리명 : {}", childCategory.getName());
			categoryRepository.delete(childCategory);
		}
		List<BookCategory> categoryLinks = bookCategoryRepository.findByCategory(category);
		List<BookCategory> toUpdate = categoryLinks.stream()
			.filter(bc -> bookCategoryRepository.countByBook(bc.getBook()) == 1)
			.peek(bc -> bc.setCategory(category.getParentCategory()))  // 상위 카테고리로 교체
			.toList();

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
		CategoryResponse response = new CategoryResponse(
			selectedCategory.getId(),
			selectedCategory.getName(),
			selectedCategory.getParentCategory() != null
				? selectedCategory.getParentCategory().getName()
				: "-",
			selectedCategory.getCategoryPath());
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
			CategoryResponse response = new CategoryResponse(
				category.getId(),
				category.getName(),
				category.getParentCategory().getName(),
				category.getCategoryPath());
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
			parents.add(current);
			log.info("경로 추가하기 위한 상위 카테고리명 :  {}", current.getName());
			current = categoryRepository.findParentCategoryById(current.getParentCategory().getId());

		}
		parents.add(current);

		for (Category category : parents) {
			CategoryResponse response = new CategoryResponse(
				category.getId(),
				category.getName(),
				category.getParentCategory() != null
					? category.getParentCategory().getName()
					: "-",
				category.getCategoryPath());
			parentcategories.add(response);
		}
		return parentcategories;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CategoryResponse> getCategories(Pageable pageable) {
		Page<Category> categoryPage = categoryRepository.findAll(pageable);
		return categoryPage.map(c ->
			new CategoryResponse(c.getId(),
				c.getName(),
				c.getParentCategory() != null
					? c.getParentCategory().getName()
					: "-",
				c.getCategoryPath())
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
	@Transactional(readOnly = true)
	public CategoryTreeResponse getCategoryTreeById(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		return toTreeDto(category);
	}

	@Override
	public Page<CategoryResponse> searchCategories(String searchKeyword, Pageable pageable) {
		Page<Category> categories = categoryRepository.searchByNameContaining(searchKeyword, pageable);
		return categories.map(
			c -> new CategoryResponse(
				c.getId(),
				c.getName(),
				c.getParentCategory() != null
					? c.getParentCategory().getName()
					: "-",
				c.getCategoryPath()));
	}

	private CategoryTreeResponse toTreeDto(Category category) {
		CategoryTreeResponse response = new CategoryTreeResponse(category.getId(), category.getName());
		for (Category child : category.getChildCategories()) {
			response.children().add(toTreeDto(child));
		}
		return response;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Category> getAllDescendantCategories(Category parent) {
		List<Category> result = new ArrayList<>();
		collectDescendants(parent, result);
		return result;
	}

	@Transactional(readOnly = true)
	protected void collectDescendants(Category category, List<Category> result) {
		for (Category child : category.getChildCategories()) {
			if (Objects.isNull(child))
				return;
			result.add(child);
			collectDescendants(child, result);
		}
	}

	@Override
	public CategoryResponse getCategoryByName(String categoryName) {
		Category category = categoryRepository.findByName(categoryName);
		return new CategoryResponse(category.getId(), category.getName(),
			category.getParentCategory() != null
				? category.getParentCategory().getName()
				: "-", category.getCategoryPath());
	}
}