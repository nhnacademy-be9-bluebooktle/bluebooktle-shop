package shop.bluebooktle.frontend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.repository.ImageServerClient;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;

@WebMvcTest(
	controllers = ImageController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = GlobalUserInfoAdvice.class
	)
)
@ActiveProfiles("test")
class ImageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ImageServerClient imageServerClient;

	@MockitoBean
	private CartService cartService;

	@MockitoBean
	private CategoryService categoryService;

	@Test
	@DisplayName("이미지 다운로드 성공 - 정상 반환")
	void proxyPngImage_success() throws Exception {
		byte[] dummyImage = new byte[] {1, 2, 3};

		when(imageServerClient.download("test-bucket", "image.png"))
			.thenReturn(dummyImage);

		mockMvc.perform(get("/images/test-bucket/image.png"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.IMAGE_PNG))
			.andExpect(content().bytes(dummyImage));
	}

	@Test
	@DisplayName("이미지 없음 - 404 반환")
	void proxyPngImage_notFound() throws Exception {
		when(imageServerClient.download("test-bucket", "notfound.png"))
			.thenReturn(null);

		mockMvc.perform(get("/images/test-bucket/notfound.png"))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("이미지 데이터 비어 있음 - 404 반환")
	void proxyPngImage_emptyImage() throws Exception {
		when(imageServerClient.download("test-bucket", "empty.png"))
			.thenReturn(new byte[0]);

		mockMvc.perform(get("/images/test-bucket/empty.png"))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("이미지 다운로드 중 예외 발생 - 500 반환")
	void proxyPngImage_serverError() throws Exception {
		when(imageServerClient.download("test-bucket", "error.png"))
			.thenThrow(new RuntimeException("IO Error"));

		mockMvc.perform(get("/images/test-bucket/error.png"))
			.andExpect(status().isInternalServerError());
	}
}
