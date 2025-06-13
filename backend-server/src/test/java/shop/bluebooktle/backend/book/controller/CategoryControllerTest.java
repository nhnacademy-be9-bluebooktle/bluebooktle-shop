package shop.bluebooktle.backend.book.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(controllers = CategoryController.class,
	excludeAutoConfiguration = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class
	}
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CategoryService categoryService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;


	@Test
	@DisplayName("모든 카테고리 페이징 조회 성공 - 검색 키워드 없음")
	@WithMockUser
	void getCategoriesWithPaginationSuccess() throws Exception {
		// given
		List<CategoryResponse> categories = List.of(
			new CategoryResponse(1L, "Category 1", null, "/1"),
			new CategoryResponse(2L, "Category 2", null, "/2")
		);
		Page<CategoryResponse> categoryPage = new PageImpl<>(categories);
		when(categoryService.getCategories(any(Pageable.class))).thenReturn(categoryPage);

		// when & then
		mockMvc.perform(get("/api/categories")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content.length()").value(categories.size()))
			.andExpect(jsonPath("$.data.content[0].categoryId").value(categories.get(0).categoryId()))
			.andExpect(jsonPath("$.data.content[0].name").value(categories.get(0).name()));

		verify(categoryService, times(1)).getCategories(any(Pageable.class));
	}

	@Test
	@DisplayName("모든 카테고리 페이징 조회 성공 - 검색 키워드 없음")
	@WithMockUser
	void getCategoriesWithPaginationWithKeywordBlankSuccess() throws Exception {
		// given
		List<CategoryResponse> categories = List.of(
			new CategoryResponse(1L, "Category 1", null, "/1"),
			new CategoryResponse(2L, "Category 2", null, "/2")
		);
		Page<CategoryResponse> categoryPage = new PageImpl<>(categories);
		when(categoryService.getCategories(any(Pageable.class))).thenReturn(categoryPage);

		// when & then
		mockMvc.perform(get("/api/categories")
				.param("page", "0")
				.param("size", "10")
				.param("searchKeyword", "")
				.accept(MediaType.APPLICATION_JSON).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content.length()").value(categories.size()))
			.andExpect(jsonPath("$.data.content[0].categoryId").value(categories.get(0).categoryId()))
			.andExpect(jsonPath("$.data.content[0].name").value(categories.get(0).name()));

		verify(categoryService, times(1)).getCategories(any(Pageable.class));
	}

	@Test
	@DisplayName("모든 카테고리 페이징 조회 성공 - 검색 키워드 있음")
	@WithMockUser
	void getCategoriesWithPaginationWithSearchKeywordSuccess() throws Exception {
		// given
		List<CategoryResponse> categories = List.of(
			new CategoryResponse(1L, "Category 1", null, "/1"),
			new CategoryResponse(2L, "Category 2", null, "/2")
		);

		Page<CategoryResponse> categoryPage = new PageImpl<>(categories);
		when(categoryService.searchCategories(anyString(), any(Pageable.class))).thenReturn(categoryPage);

		String searchKeyword = "Category";
		// when & then
		mockMvc.perform(get("/api/categories")
				.param("page", "0")
				.param("size", "10")
				.param("searchKeyword", searchKeyword)
				.accept(MediaType.APPLICATION_JSON).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content.length()").value(categories.size()))
			.andExpect(jsonPath("$.data.content[0].categoryId").value(categories.get(0).categoryId()))
			.andExpect(jsonPath("$.data.content[0].name").value(categories.get(0).name()));

		verify(categoryService, times(1)).searchCategories(eq(searchKeyword), any(Pageable.class));
	}



	@Test
	@DisplayName("최상위 카테고리 트리 조회 성공")
	@WithMockUser
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
				.accept(MediaType.APPLICATION_JSON).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.length()").value(categoryTree.size()))
			.andExpect(jsonPath("$.data[0].id").value(categoryTree.get(0).id()))
			.andExpect(jsonPath("$.data[0].name").value(categoryTree.get(0).name()))
			.andExpect(jsonPath("$.data[0].children.length()").value(categoryTree.get(0).children().size()));

		verify(categoryService, times(1)).getCategoryTree();
	}

	@Test
	@DisplayName("최상위 카테고리 등록 성공")
	@WithMockUser
	void registerRootCategorySuccess() throws Exception {
		// given
		RootCategoryRegisterRequest request = new RootCategoryRegisterRequest("New Root Category", "New Description");
		String content = objectMapper.writeValueAsString(request);

		doNothing().when(categoryService).registerRootCategory(any(RootCategoryRegisterRequest.class));

		// when & then
		mockMvc.perform(post("/api/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content).with(csrf()))
			.andExpect(status().isCreated());

		verify(categoryService, times(1)).registerRootCategory(any(RootCategoryRegisterRequest.class));
	}

	@Test
	@DisplayName("특정 상위 카테고리에 하위 카테고리 추가 성공")
	@WithMockUser
	void addCategorySuccess() throws Exception {
		// given
		Long parentCategoryId = 1L;
		CategoryRegisterRequest request = new CategoryRegisterRequest("New Subcategory");
		String content = objectMapper.writeValueAsString(request);

		doNothing().when(categoryService).registerCategory(anyLong(), any(CategoryRegisterRequest.class));

		// when & then
		mockMvc.perform(post("/api/categories/{parentCategoryId}", parentCategoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content).with(csrf()))
			.andExpect(status().isCreated());

		verify(categoryService, times(1))
			.registerCategory(eq(parentCategoryId), any(CategoryRegisterRequest.class));
	}

	@Test
	@DisplayName("특정 카테고리 조회 성공")
	@WithMockUser
	void getCategorySuccess() throws Exception {
		// given
		Long categoryId = 1L;
		CategoryResponse response = new CategoryResponse(categoryId, "Category Name", null, "");

		when(categoryService.getCategory(categoryId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/categories/{categoryId}", categoryId)
				.accept(MediaType.APPLICATION_JSON).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.categoryId").value(response.categoryId()))
			.andExpect(jsonPath("$.data.name").value(response.name()));

		verify(categoryService, times(1)).getCategory(categoryId);
	}

	@Test
	@DisplayName("카테고리 삭제 성공")
	@WithMockUser
	void deleteCategorySuccess() throws Exception {
		// given
		Long categoryId = 1L;

		doNothing().when(categoryService).deleteCategory(categoryId);

		// when & then
		mockMvc.perform(delete("/api/categories/{categoryId}", categoryId).with(csrf()))
			.andExpect(status().isOk());

		verify(categoryService, times(1)).deleteCategory(categoryId);
	}

	@Test
	@DisplayName("카테고리명 수정 성공")
	@WithMockUser
	void updateCategorySuccess() throws Exception {
		// given
		Long categoryId = 1L;
		CategoryUpdateRequest request = new CategoryUpdateRequest("Updated Name");
		String content = objectMapper.writeValueAsString(request);

		doNothing().when(categoryService).updateCategory(anyLong(), any(CategoryUpdateRequest.class));

		// when & then
		mockMvc.perform(put("/api/categories/{categoryId}", categoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content).with(csrf()))
			.andExpect(status().isOk());

		verify(categoryService, times(1)).updateCategory(eq(categoryId), any(CategoryUpdateRequest.class));
	}


	@Test
	@DisplayName("최상위 카테고리 이름으로 조회 성공")
	@WithMockUser
	void getCategoryByNameSuccess() throws Exception {
		String categoryName = "Root Category";
		CategoryResponse response = new CategoryResponse(1L, "Root Category", null, "/1");

		when(categoryService.getCategoryByName(anyString())).thenReturn(response);

		mockMvc.perform(get("/api/categories/name/{categoryName}", categoryName)
				.accept(MediaType.APPLICATION_JSON).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.categoryId").value(response.categoryId()))
			.andExpect(jsonPath("$.data.name").value(response.name()));

		verify(categoryService, times(1)).getCategoryByName(eq(categoryName));
	}



}