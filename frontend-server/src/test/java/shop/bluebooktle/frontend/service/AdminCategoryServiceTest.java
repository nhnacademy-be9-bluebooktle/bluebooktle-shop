package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.CategoryRepository;
import shop.bluebooktle.frontend.service.impl.AdminCategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class AdminCategoryServiceTest {
	@Mock
	CategoryRepository categoryRepository;

	@InjectMocks
	AdminCategoryServiceImpl categoryService;

	@Test
	@DisplayName("카테고리 트리 조회 성공")
	void getCategoryTree_success() {
		List<CategoryTreeResponse> mockTree = List.of(
			new CategoryTreeResponse(1L, "문학", List.of())
		);
		when(categoryRepository.allCategoriesTree()).thenReturn(mockTree);

		List<CategoryTreeResponse> result = categoryService.getCategoryTree();

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().name()).isEqualTo("문학");
	}

	@Test
	@DisplayName("카테고리 목록 조회 성공")
	void getCategories_success() {
		int page = 0, size = 10;
		String keyword = "카테고리";
		List<CategoryResponse> list = List.of(
			new CategoryResponse(1L, "소설", "문학", "/1/2")
		);
		PaginationData<CategoryResponse> mockData = new PaginationData<>(
			list,
			new PaginationData.PaginationInfo(
				1,     // totalPages
				1L,    // totalElements
				0,     // currentPage
				10,    // pageSize
				true,  // isFirst
				true,  // isLast
				false, // hasNext
				false  // hasPrevious
			)
		);

		when(categoryRepository.getPagedCategories(page, size, keyword)).thenReturn(mockData);

		Page<CategoryResponse> result = categoryService.getCategories(page, size, keyword);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().name()).isEqualTo("소설");
	}

	@Test
	@DisplayName("카테고리 단건 조회 성공")
	void getCategory_success() {
		CategoryResponse response = new CategoryResponse(2L, "에세이", "문학", "/1/3");
		when(categoryRepository.getCategory(2L)).thenReturn(response);

		CategoryResponse result = categoryService.getCategory(2L);

		assertThat(result.categoryId()).isEqualTo(2L);
		assertThat(result.name()).isEqualTo("에세이");
	}

	@Test
	@DisplayName("최상위 카테고리 추가")
	void addRootCategory_success() {
		RootCategoryRegisterRequest request = new RootCategoryRegisterRequest("문학", "소설");

		categoryService.addRootCategory(request);

		verify(categoryRepository, times(1)).addRootCategory(request);
	}

	@Test
	@DisplayName("하위 카테고리 추가")
	void addCategory_success() {
		Long parentId = 1L;
		CategoryRegisterRequest request = new CategoryRegisterRequest("소설");

		categoryService.addCategory(parentId, request);

		verify(categoryRepository, times(1)).addCategory(parentId, request);
	}

	@Test
	@DisplayName("카테고리 수정")
	void updateCategory_success() {
		Long id = 1L;
		CategoryUpdateRequest request = new CategoryUpdateRequest("수정된이름");

		categoryService.updateCategory(id, request);

		verify(categoryRepository, times(1)).updateCategory(id, request);
	}

	@Test
	@DisplayName("카테고리 삭제")
	void deleteCategory_success() {
		Long id = 5L;

		categoryService.deleteCategory(id);

		verify(categoryRepository, times(1)).deleteCategory(id);
	}
}
