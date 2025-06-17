package shop.bluebooktle.backend.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.BookImgService;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(BookImgController.class)
@ActiveProfiles("test")
class BookImgControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private BookImgService bookImgService;
	@MockitoBean
	private JwtUtil jwtUtil;
	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@WithMockUser
	@DisplayName("도서 이미지 조회 성공")
	void getImagesByBook_success() throws Exception {
		Long bookId = 1L;
		LocalDateTime fixedDateTime = LocalDateTime.of(2025, 6, 16, 20, 47, 3, 345_993_400);
		ImgResponse response = ImgResponse.builder()
			.imgUrl("https://cdn.example.com/thumbnail.jpg")
			.createdAt(fixedDateTime)
			.build();

		when(bookImgService.getImgByBookId(bookId)).thenReturn(response);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

		mockMvc.perform(get("/api/books/{book-id}/images", bookId)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.imgUrl").value(response.getImgUrl()))
			.andExpect(jsonPath("$.data.createdAt").value(fixedDateTime.format(formatter)));

		verify(bookImgService, times(1)).getImgByBookId(bookId);
	}
}
