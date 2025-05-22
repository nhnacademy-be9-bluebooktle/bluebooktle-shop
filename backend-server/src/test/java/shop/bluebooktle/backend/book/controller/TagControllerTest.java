package shop.bluebooktle.backend.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.TagService;
import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.service.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(TagController.class)
class TagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TagService tagService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("태그 등록 성공")
	@WithMockUser
	void testAddTag() throws Exception {
		TagRequest request = new TagRequest("New Tag");

		mockMvc.perform(post("/api/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)).with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		verify(tagService, times(1)).registerTag(any(TagRequest.class));
	}

	@Test
	@DisplayName("태그 수정 성공")
	@WithMockUser
	void testUpdateTag() throws Exception {
		Long tagId = 1L;
		TagRequest request = new TagRequest("Updated Tag");

		mockMvc.perform(put("/api/tags/{tagId}", tagId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(tagService, times(1)).updateTag(eq(tagId), any(TagRequest.class));
	}

	@Test
	@DisplayName("태그 삭제 성공")
	@WithMockUser
	void testDeleteTag() throws Exception {
		Long tagId = 1L;

		mockMvc.perform(delete("/api/tags/{tagId}", tagId).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(tagService, times(1)).deleteTag(tagId);
	}

	@Test
	@DisplayName("태그 조회 성공")
	@WithMockUser
	void testGetTag() throws Exception {
		Long tagId = 1L;
		TagInfoResponse response = new TagInfoResponse(tagId, "Tag Name", LocalDateTime.now());
		when(tagService.getTag(tagId)).thenReturn(response);

		mockMvc.perform(get("/api/tags/{tagId}", tagId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(tagId))
			.andExpect(jsonPath("$.data.name").value("Tag Name"));

		verify(tagService, times(1)).getTag(tagId);
	}

	@Test
	@DisplayName("태그 목록 조회 성공")
	@WithMockUser
	void testGetTags() throws Exception {
		TagInfoResponse tag1 = new TagInfoResponse(1L, "Tag 1", LocalDateTime.now());
		TagInfoResponse tag2 = new TagInfoResponse(2L, "Tag 2", LocalDateTime.now());
		Page<TagInfoResponse> tagPage = new PageImpl<>(List.of(tag1, tag2));
		PaginationData<TagInfoResponse> paginationData = new PaginationData<>(tagPage);
		when(tagService.getTags(any(Pageable.class))).thenReturn(tagPage);

		mockMvc.perform(get("/api/tags").param("size", "10").param("sort", "id,asc"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].name").value("Tag 1"))
			.andExpect(jsonPath("$.data.content[1].name").value("Tag 2"));

		verify(tagService, times(1)).getTags(any(Pageable.class));
	}
}
