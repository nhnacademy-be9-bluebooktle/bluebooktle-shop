package shop.bluebooktle.backend.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.ImgService;
import shop.bluebooktle.backend.book.service.MinioService;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(ImgController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ImgControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ImgService imgService;
	@MockitoBean
	private MinioService minioService;
	@MockitoBean
	private JwtUtil jwtUtil;
	@MockitoBean
	private AuthUserLoader authUserLoader;


	@Test
	@DisplayName("이미지 삭제 - 사용자 권한")
	void deleteImg_user() throws Exception {
		mockMvc.perform(delete("/api/imgs/{image-id}", 1L)) // 👈 JWT 흉내
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(imgService, times(1)).deleteImg(1L);
	}

	@Test
	@DisplayName("MinIO 이미지 삭제 - 관리자 권한")
	void deleteImage_admin() throws Exception {
		String fileName = "test.jpg";

		mockMvc.perform(delete("/api/imgs/minioUrl")
				.param("fileName", fileName))// 👈 JWT 흉내
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(minioService, times(1)).deleteImage(fileName);
	}

	@Test
	@DisplayName("Presigned Upload URL 발급 성공")
	void getPresignedUploadUrl_success() throws Exception {
		String fileName = "sample.jpg";
		String url = "https://minio.example.com/presigned-url";

		when(minioService.getPresignedUploadUrl(fileName)).thenReturn(url);

		mockMvc.perform(get("/api/imgs/presignedUploadUrl")
				.param("fileName", fileName)) // 👈 JWT 흉내
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value(url));

		verify(minioService, times(1)).getPresignedUploadUrl(fileName);
	}

	@Test
	@DisplayName("리뷰 이미지 조회 - 성공")
	void getImgByReviewId_success() throws Exception {
		// given
		Long reviewId = 1L;
		LocalDateTime createdAt = LocalDateTime.of(2025, 6, 16, 22, 0);
		ImgResponse response = ImgResponse.builder()
			.imgUrl("https://cdn.example.com/review.jpg")
			.createdAt(createdAt)
			.build();

		when(imgService.getImgByReviewId(reviewId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/imgs/by-review/{review-id}", reviewId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.imgUrl").value(response.getImgUrl()))
			.andExpect(jsonPath("$.data.createdAt").value("2025-06-16T22:00:00")); // <-- 문자열로 기대값 지정
	}
}
