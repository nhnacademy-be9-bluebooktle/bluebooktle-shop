package shop.bluebooktle.backend.book.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;

@WebMvcTest(controllers = CategoryController.class,
	excludeAutoConfiguration = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class
	}
)
@AutoConfigureMockMvc
public class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CategoryService categoryService;

	@Test
	@DisplayName("모든 카테고리 페이징 조회 성공")
	void getCategoriesWithPaginationSuccess() throws Exception {
		// given
		List<CategoryResponse> categories = List.of(
			new CategoryResponse(1L, "Category 1"),
			new CategoryResponse(2L, "Category 2")
		);
		Page<CategoryResponse> categoryPage = new PageImpl<>(categories);
		when(categoryService.getCategories(any(Pageable.class))).thenReturn(categoryPage);

		// when & then
		mockMvc.perform(get("/api/categories")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content.length()").value(categories.size()))
			.andExpect(jsonPath("$.data.content[0].categoryId").value(categories.get(0).categoryId()))
			.andExpect(jsonPath("$.data.content[0].name").value(categories.get(0).name()));

		verify(categoryService, times(1)).getCategories(any(Pageable.class));
	}

	@Test
	@DisplayName("최상위 카테고리 트리 조회 성공")
	void getCategoryTreeSuccess() throws Exception {
		// given
		List<CategoryTreeResponse> categoryTree = List.of(
			new CategoryTreeResponse(1L, "Root Category", List.of(
				new CategoryTreeResponse(2L, "Child Category", List.of())
			))
		);
		when(categoryService.getCategoryTree()).thenReturn(categoryTree);

		// when & then
		mockMvc.perform(get("/api/categories/tree")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.length()").value(categoryTree.size()))
			.andExpect(jsonPath("$.data[0].id").value(categoryTree.get(0).id()))
			.andExpect(jsonPath("$.data[0].name").value(categoryTree.get(0).name()))
			.andExpect(jsonPath("$.data[0].children.length()").value(categoryTree.get(0).children().size()));

		verify(categoryService, times(1)).getCategoryTree();
	}

	@Test
	@DisplayName("특정 카테고리 트리 조회 성공")
	void getCategoryTreeByIdSuccess() throws Exception {
		// given
		Long categoryId = 1L;
		CategoryTreeResponse categoryTree = new CategoryTreeResponse(categoryId, "카테고리1", List.of(
			new CategoryTreeResponse(2L, "카테고리1_1", List.of()),
			new CategoryTreeResponse(3L, "카테고리1_2", List.of()),
			new CategoryTreeResponse(4L, "카테고리1_3", List.of())
		));
		when(categoryService.getCategoryTreeById(categoryId)).thenReturn(categoryTree);

		// when & then
		mockMvc.perform(get("/api/categories/{categoryId}/tree", categoryId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(categoryTree.id()))
			.andExpect(jsonPath("$.data.name").value(categoryTree.name()))
			.andExpect(jsonPath("$.data.children.length()").value(categoryTree.children().size()))
			.andExpect(jsonPath("$.data.children[0].id").value(2))
			.andExpect(jsonPath("$.data.children[0].name").value("카테고리1_1"))
			.andExpect(jsonPath("$.data.children[1].id").value(3))
			.andExpect(jsonPath("$.data.children[1].name").value("카테고리1_2"))
			.andExpect(jsonPath("$.data.children[2].id").value(4))
			.andExpect(jsonPath("$.data.children[2].name").value("카테고리1_3"));

		verify(categoryService, times(1)).getCategoryTreeById(categoryId);
	}

	@Test
	@DisplayName("최상위 카테고리 등록 성공")
	void registerRootCategorySuccess() throws Exception {
		// given
		RootCategoryRegisterRequest request = new RootCategoryRegisterRequest("New Root Category", "New Description");
		String content = objectMapper.writeValueAsString(request);

		doNothing().when(categoryService).registerRootCategory(any(RootCategoryRegisterRequest.class));

		// when & then
		mockMvc.perform(post("/api/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
			.andExpect(status().isCreated());

		verify(categoryService, times(1)).registerRootCategory(any(RootCategoryRegisterRequest.class));
	}

	@Test
	@DisplayName("특정 상위 카테고리에 하위 카테고리 추가 성공")
	void addCategorySuccess() throws Exception {
		// given
		Long parentCategoryId = 1L;
		CategoryRegisterRequest request = new CategoryRegisterRequest("New Subcategory");
		String content = objectMapper.writeValueAsString(request);

		doNothing().when(categoryService).registerCategory(anyLong(), any(CategoryRegisterRequest.class));

		// when & then
		mockMvc.perform(post("/api/categories/{parentCategoryId}", parentCategoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
			.andExpect(status().isCreated());

		verify(categoryService, times(1))
			.registerCategory(eq(parentCategoryId), any(CategoryRegisterRequest.class));
	}

	@Test
	@DisplayName("특정 카테고리 조회 성공")
	void getCategorySuccess() throws Exception {
		// given
		Long categoryId = 1L;
		CategoryResponse response = new CategoryResponse(categoryId, "Category Name");

		when(categoryService.getCategory(categoryId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/categories/{categoryId}", categoryId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.categoryId").value(response.categoryId()))
			.andExpect(jsonPath("$.data.name").value(response.name()));

		verify(categoryService, times(1)).getCategory(categoryId);
	}

	@Test
	@DisplayName("카테고리 삭제 성공")
	void deleteCategorySuccess() throws Exception {
		// given
		Long categoryId = 1L;

		doNothing().when(categoryService).deleteCategory(categoryId);

		// when & then
		mockMvc.perform(delete("/api/categories/{categoryId}", categoryId))
			.andExpect(status().isOk());

		verify(categoryService, times(1)).deleteCategory(categoryId);
	}

	@Test
	@DisplayName("카테고리명 수정 성공")
	void updateCategorySuccess() throws Exception {
		// given
		Long categoryId = 1L;
		CategoryUpdateRequest request = new CategoryUpdateRequest("Updated Name");
		String content = objectMapper.writeValueAsString(request);

		doNothing().when(categoryService).updateCategory(anyLong(), any(CategoryUpdateRequest.class));

		// when & then
		mockMvc.perform(put("/api/categories/{categoryId}", categoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
			.andExpect(status().isOk());

		verify(categoryService, times(1)).updateCategory(eq(categoryId), any(CategoryUpdateRequest.class));
	}

	@Test
	@DisplayName("특정 상위 카테고리의 하위 카테고리 목록 조회 성공")
	void getSubcategoriesSuccess() throws Exception {
		// given
		Long parentCategoryId = 1L;
		List<CategoryResponse> subcategories = List.of(
			new CategoryResponse(2L, "Subcategory 1"),
			new CategoryResponse(3L, "Subcategory 2")
		);

		when(categoryService.getSubcategoriesByParentCategoryId(parentCategoryId))
			.thenReturn(subcategories);

		// when & then
		mockMvc.perform(get("/api/categories/{parentCategoryId}/subcategories", parentCategoryId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.length()").value(subcategories.size()))
			.andExpect(jsonPath("$.data[0].categoryId").value(subcategories.get(0).categoryId()))
			.andExpect(jsonPath("$.data[0].name").value(subcategories.get(0).name()));

		verify(categoryService, times(1)).getSubcategoriesByParentCategoryId(parentCategoryId);
	}

	@Test
	@DisplayName("특정 하위 카테고리가 포함된 상위 카테고리 목록 조회 성공")
	void getParentCategoriesSuccess() throws Exception {
		// given
		Long childCategoryId = 1L;
		List<CategoryResponse> parentCategories = List.of(
			new CategoryResponse(2L, "Parent Category 1"),
			new CategoryResponse(3L, "Parent Category 2")
		);

		when(categoryService.getParentCategoriesByLeafCategoryId(childCategoryId))
			.thenReturn(parentCategories);

		// when & then
		mockMvc.perform(get("/api/categories/{categoryId}/parentcategories", childCategoryId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.length()").value(parentCategories.size()))
			.andExpect(jsonPath("$.data[0].categoryId").value(parentCategories.get(0).categoryId()))
			.andExpect(jsonPath("$.data[0].name").value(parentCategories.get(0).name()));

		verify(categoryService, times(1)).getParentCategoriesByLeafCategoryId(childCategoryId);
	}
}