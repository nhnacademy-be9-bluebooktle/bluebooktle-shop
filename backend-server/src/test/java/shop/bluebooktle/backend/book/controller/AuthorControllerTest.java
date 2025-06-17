package shop.bluebooktle.backend.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.AuthorService;
import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(AuthorController.class)
@ActiveProfiles("test")
class AuthorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AuthorService authorService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("작가 등록 성공")
	@WithMockUser
	void testRegisterAuthor() throws Exception {
		AuthorRequest authorRequest = AuthorRequest.builder()
			.name("New Author")
			.build();

		doNothing().when(authorService).registerAuthor(any());

		mockMvc.perform(post("/api/authors")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authorRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		verify(authorService, times(1)).registerAuthor(any(AuthorRegisterRequest.class));
	}

	@Test
	@DisplayName("작가 조회 성공")
	@WithMockUser
	void testGetAuthor() throws Exception {
		Long authorId = 1L;
		AuthorResponse authorResponse = AuthorResponse.builder()
			.id(authorId)
			.name("Author")
			.createdAt(LocalDateTime.now())
			.build();

		when(authorService.getAuthor(authorId)).thenReturn(authorResponse);

		mockMvc.perform(get("/api/authors/{authorId}", authorId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(authorId))
			.andExpect(jsonPath("$.data.name").value("Author"));

		verify(authorService, times(1)).getAuthor(authorId);
	}

	@Test
	@DisplayName("작가 목록 조회 성공")
	@WithMockUser
	void testGetPagedAuthors() throws Exception {

		AuthorResponse authorResponse1 = AuthorResponse.builder()
			.id(1L)
			.name("Author1")
			.build();
		AuthorResponse authorResponse2 = AuthorResponse.builder()
			.id(2L)
			.name("Author2")
			.build();

		Page<AuthorResponse> page = new PageImpl<>(
			Arrays.asList(authorResponse1, authorResponse2),
			PageRequest.of(0, 10),
			2
		);

		when(authorService.getAuthors(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/authors")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(1))
			.andExpect(jsonPath("$.data.content[0].name").value("Author1"))
			.andExpect(jsonPath("$.data.content[1].id").value(2))
			.andExpect(jsonPath("$.data.content[1].name").value("Author2"));

		verify(authorService, times(1)).getAuthors(any(PageRequest.class));
	}

	@Test
	@DisplayName("작가 목록 조회 성공 - 검색어 없음")
	@WithMockUser
	void testGetPagedAuthors_noKeyword() throws Exception {
		AuthorResponse author1 = AuthorResponse.builder().id(1L).name("Author1").build();
		AuthorResponse author2 = AuthorResponse.builder().id(2L).name("Author2").build();

		Page<AuthorResponse> page = new PageImpl<>(
			List.of(author1, author2),
			PageRequest.of(0, 10),
			2
		);

		when(authorService.getAuthors(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/authors")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(1))
			.andExpect(jsonPath("$.data.content[0].name").value("Author1"))
			.andExpect(jsonPath("$.data.content[1].id").value(2))
			.andExpect(jsonPath("$.data.content[1].name").value("Author2"));

		verify(authorService, times(1)).getAuthors(any(PageRequest.class));
	}

	@Test
	@DisplayName("작가 목록 조회 성공 - 검색어 존재")
	@WithMockUser
	void testGetPagedAuthors_withKeyword() throws Exception {
		AuthorResponse author = AuthorResponse.builder().id(3L).name("검색된 작가").build();

		Page<AuthorResponse> page = new PageImpl<>(
			List.of(author),
			PageRequest.of(0, 10),
			1
		);

		when(authorService.searchAuthors(eq("검색"), any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/authors")
				.param("page", "0")
				.param("size", "10")
				.param("searchKeyword", "검색"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(3))
			.andExpect(jsonPath("$.data.content[0].name").value("검색된 작가"));

		verify(authorService, times(1)).searchAuthors(eq("검색"), any(PageRequest.class));
	}

	@Test
	@DisplayName("작가 목록 조회 성공 - 공백 검색어 (isBlank=true)")
	@WithMockUser
	void testGetPagedAuthors_blankKeyword() throws Exception {
		AuthorResponse author = AuthorResponse.builder().id(10L).name("Blank Author").build();

		Page<AuthorResponse> page = new PageImpl<>(
			List.of(author),
			PageRequest.of(0, 10),
			1
		);

		when(authorService.getAuthors(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/authors")
				.param("page", "0")
				.param("size", "10")
				.param("searchKeyword", " ")) // 공백 문자열
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(10))
			.andExpect(jsonPath("$.data.content[0].name").value("Blank Author"));

		verify(authorService, times(1)).getAuthors(any(PageRequest.class));
		verify(authorService, never()).searchAuthors(anyString(), any(PageRequest.class));
	}

	@Test
	@DisplayName("작가 수정 성공")
	@WithMockUser
	void testUpdateAuthor() throws Exception {
		Long authorId = 1L;
		AuthorRequest authorRequest = AuthorRequest.builder()
			.name("Updated Author")
			.build();

		mockMvc.perform(put("/api/authors/{authorId}", authorId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authorRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(authorService, times(1)).updateAuthor(eq(authorId), any(AuthorUpdateRequest.class));
	}

	@Test
	@DisplayName("작가 삭제 성공")
	@WithMockUser
	void testDeleteAuthor() throws Exception {
		Long authorId = 1L;

		mockMvc.perform(delete("/api/authors/{authorId}", authorId).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(authorService, times(1)).deleteAuthor(authorId);
	}
}
