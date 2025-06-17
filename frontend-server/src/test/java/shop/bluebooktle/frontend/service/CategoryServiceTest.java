package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.frontend.repository.CategoryRepository;
import shop.bluebooktle.frontend.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryServiceImpl categoryService;

	@Test
	@DisplayName("카테고리 트리 캐시된 결과 반환")
	void getCategoryTreeCached_success() {
		// given
		List<CategoryTreeResponse> mockTree = List.of(
			new CategoryTreeResponse(1L, "문학", List.of())
		);
		when(categoryRepository.allCategoriesTree()).thenReturn(mockTree);

		// when
		List<CategoryTreeResponse> result = categoryService.getCategoryTreeCached();

		// then
		assertThat(result).isEqualTo(mockTree);
		verify(categoryRepository, times(1)).allCategoriesTree();
	}

	@Test
	@DisplayName("카테고리 이름으로 조회 성공")
	void getCategoryByName_success() {
		// given
		String categoryName = "소설";
		CategoryResponse mockResponse = new CategoryResponse(2L, "소설", "문학", "/1/2");
		when(categoryRepository.getCategoryByName(categoryName)).thenReturn(mockResponse);

		// when
		CategoryResponse result = categoryService.getCategoryByName(categoryName);

		// then
		assertThat(result).isEqualTo(mockResponse);
		verify(categoryRepository, times(1)).getCategoryByName(categoryName);
	}
}
