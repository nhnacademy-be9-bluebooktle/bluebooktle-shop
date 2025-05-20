package shop.bluebooktle.backend.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;

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

	@Test
	@DisplayName("카테고리 등록 성공")
	void registerCategorySuccess() throws Exception {
		// given
		CategoryRegisterRequest req = new CategoryRegisterRequest("테스트");
		String json = objectMapper.writeValueAsString(req);
		doNothing().when(categoryService).registerCategory(any(), any());

		// when / then
		mockMvc.perform(post("/api/categories/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isCreated());

		// verify
		verify(categoryService, times(1)).registerCategory(anyLong(), any(CategoryRegisterRequest.class));
	}

}
