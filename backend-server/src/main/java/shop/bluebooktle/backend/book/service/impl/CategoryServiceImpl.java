package shop.bluebooktle.backend.book.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
		Category parent = categoryRepository.findById(parentCategoryId)
			.orElseThrow(() -> new CategoryNotFoundException(parentCategoryId));

		StringBuilder categoryPathBuilder = new StringBuilder();
		
		// 부모 카테고리의 하위 카테고리 조회
		List<Category> categoryList = categoryRepository.getAllDescendantCategories(parent);

		boolean nameExists = categoryList.stream()
			.anyMatch(c -> c.getName().equals(request.name()));
		// 부모 카테고리의 하위카테고리 중 중복된 이름으로 등록 불가능 : 예외 발생
		if (nameExists) {
			throw new CategoryAlreadyExistsException("이미 존재하는 카테고리명입니다. 카테고리명: " + request.name());
		}

		// 등록할 카테고리 생성
		Category newCategory = Category.builder()
			.name(request.name())
			.parentCategory(parent)
			.build();

		newCategory = categoryRepository.save(newCategory);

		categoryPathBuilder.append(parent.getCategoryPath());
		categoryPathBuilder.append("/");
		categoryPathBuilder.append(newCategory.getId());
		String categoryPathStr = categoryPathBuilder.toString();

		newCategory.setCategoryPath(categoryPathStr);
		categoryRepository.save(newCategory);

		parent.addChildCategory(newCategory);  // 연관 관계 설정
		categoryRepository.save(parent);       // dirty checking 보장 목적
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
			List<Category> categoryList = categoryRepository.getAllDescendantCategories(category.getParentCategory());
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

		if (bookCategoryRepository.existsByCategory(category)) {
			throw new CategoryCannotDeleteRootException("(도서가 등록된 카테고리 삭제 불가)");
		}
		// 하위 모든 카테고리 수집
		List<Category> descendants = categoryRepository.getAllDescendantCategories(category);
		for (Category descendant : descendants) {
			if (bookCategoryRepository.existsByCategory(descendant)) {
				throw new CategoryCannotDeleteRootException("(도서가 등록된 하위 카테고리 존재시 삭제 불가)");
			}
		}

		// 최상위 카테고리는 삭제 불가
		if (categoryRepository.existsByIdAndParentCategoryIsNull(category.getId())) {
			throw new CategoryCannotDeleteRootException("(최상위 카테고리 삭제 불가)");
		}
		// 상위 카테고리가 최상위 카테고리이면서 2단계 카테고리가 1개일 경우 삭제 불가능
		if (categoryRepository.existsByIdAndParentCategoryIsNull(category.getParentCategory().getId())) {
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
		List<Category> allCategories = categoryRepository.findAll();
		Map<Long, List<Category>> parentIdToChildrenMap = allCategories.stream()
			.filter(c -> c.getParentCategory() != null)
			.collect(Collectors.groupingBy(c -> c.getParentCategory().getId()));

		List<CategoryTreeResponse> tree = allCategories.stream()
			.filter(c -> c.getParentCategory() == null)
			.map(c -> buildTree(c, parentIdToChildrenMap))
			.toList();
		return tree;
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

	@Override
	public CategoryResponse getCategoryByName(String categoryName) {
		Category category = categoryRepository.findByName(categoryName);
		return new CategoryResponse(category.getId(), category.getName(),
			category.getParentCategory() != null
				? category.getParentCategory().getName()
				: "-", category.getCategoryPath());
	}

	private CategoryTreeResponse buildTree(Category category, Map<Long, List<Category>> parentMap) {
		List<CategoryTreeResponse> children = parentMap.getOrDefault(category.getId(), List.of()).stream()
			.map(child -> buildTree(child, parentMap))
			.collect(Collectors.toList());

		return new CategoryTreeResponse(category.getId(), category.getName(), children);
	}
}