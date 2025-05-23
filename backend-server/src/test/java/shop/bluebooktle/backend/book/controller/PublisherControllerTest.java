package shop.bluebooktle.backend.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(PublisherController.class)
class PublisherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PublisherService publisherService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("출판사 등록 성공")
	@WithMockUser
	void testAddPublisher() throws Exception {
		PublisherRequest request = PublisherRequest.builder()
			.name("New Publisher")
			.build();

		mockMvc.perform(post("/api/publishers")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		verify(publisherService, times(1)).registerPublisher(any(PublisherRequest.class));
	}

	@Test
	@DisplayName("출판사 수정 성공")
	@WithMockUser
	void testUpdatePublisher() throws Exception {
		Long publisherId = 1L;
		PublisherRequest request = PublisherRequest.builder()
			.name("Updated Publisher")
			.build();

		mockMvc.perform(put("/api/publishers/{publisherId}", publisherId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(publisherService, times(1)).updatePublisher(eq(publisherId), any(PublisherRequest.class));
	}

	@Test
	@DisplayName("출판사 삭제 성공")
	@WithMockUser
	void testDeletePublisher() throws Exception {
		Long publisherId = 1L;

		mockMvc.perform(delete("/api/publishers/{publisherId}", publisherId).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(publisherService, times(1)).deletePublisher(publisherId);
	}

	@Test
	@DisplayName("출판사 조회 성공")
	@WithMockUser
	void testGetPublisher() throws Exception {
		Long publisherId = 1L;
		PublisherInfoResponse response = PublisherInfoResponse.builder()
			.id(publisherId)
			.name("Publisher 1")
			.build();

		when(publisherService.getPublisher(publisherId)).thenReturn(response);

		mockMvc.perform(get("/api/publishers/{publisherId}", publisherId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(publisherId))
			.andExpect(jsonPath("$.data.name").value("Publisher 1"));

		verify(publisherService, times(1)).getPublisher(publisherId);
	}

	@Test
	@DisplayName("출판사 목록 조회 성공")
	@WithMockUser
	void testGetPublishers() throws Exception {
		PublisherInfoResponse publisher1 = PublisherInfoResponse.builder()
			.id(1L)
			.name("Publisher 1")
			.build();

		PublisherInfoResponse publisher2 = PublisherInfoResponse.builder()
			.id(2L)
			.name("Publisher 2")
			.build();

		Page<PublisherInfoResponse> page = new PageImpl<>(
			Arrays.asList(publisher1, publisher2),
			PageRequest.of(0, 10),
			2
		);
		PaginationData<PublisherInfoResponse> paginationData = new PaginationData<>(page);

		when(publisherService.getPublishers(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/publishers")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(1))
			.andExpect(jsonPath("$.data.content[0].name").value("Publisher 1"))
			.andExpect(jsonPath("$.data.content[1].id").value(2))
			.andExpect(jsonPath("$.data.content[1].name").value("Publisher 2"));

		verify(publisherService, times(1)).getPublishers(any(PageRequest.class));
	}
}