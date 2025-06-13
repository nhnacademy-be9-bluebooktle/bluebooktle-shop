package shop.bluebooktle.backend.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(PublisherController.class)
@ActiveProfiles("test")
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
	void addPublisher() throws Exception {
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
	void updatePublisher() throws Exception {
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
	void deletePublisher() throws Exception {
		Long publisherId = 1L;

		mockMvc.perform(delete("/api/publishers/{publisherId}", publisherId).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(publisherService, times(1)).deletePublisher(publisherId);
	}

	@Test
	@DisplayName("출판사 조회 성공")
	@WithMockUser
	void getPublisher() throws Exception {
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
	@DisplayName("출판사 목록 조회 - 검색어가 있는 경우")
	@WithMockUser
	void getPublishersWithSearchKeyword() throws Exception {
		String searchKeyword = "한꿈";

		PublisherInfoResponse publisher = PublisherInfoResponse.builder()
			.id(1L)
			.name("한꿈출판사")
			.build();
		Page<PublisherInfoResponse> page = new PageImpl<>(
			List.of(publisher),
			PageRequest.of(0, 10),
			1
		);

		when(publisherService.searchPublishers(eq(searchKeyword), any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/publishers")
				.param("page", "0")
				.param("size", "10")
				.param("searchKeyword", searchKeyword))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].name").value("한꿈출판사"));

		verify(publisherService, times(1)).searchPublishers(eq(searchKeyword), any(Pageable.class));
	}

	@Test
	@DisplayName("출판사 목록 조회 - 검색어가 null인 경우")
	@WithMockUser
	void getPublishersWithoutSearchKeyword() throws Exception {
		PublisherInfoResponse publisher = PublisherInfoResponse.builder()
			.id(1L)
			.name("한꿈출판사")
			.build();
		Page<PublisherInfoResponse> page = new PageImpl<>(
			List.of(publisher),
			PageRequest.of(0, 10),
			1
		);

		when(publisherService.getPublishers(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/publishers")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].name").value("한꿈출판사"));

		verify(publisherService, times(1)).getPublishers(any(Pageable.class));
	}

	@Test
	@DisplayName("출판사 목록 조회 - 검색어가 blank인 경우")
	@WithMockUser
	void getPublishersWithBlankSearchKeyword() throws Exception {
		PublisherInfoResponse publisher = PublisherInfoResponse.builder()
			.id(1L)
			.name("한꿈출판사")
			.build();
		Page<PublisherInfoResponse> page = new PageImpl<>(
			List.of(publisher),
			PageRequest.of(0, 10),
			1
		);

		when(publisherService.getPublishers(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/publishers")
				.param("page", "0")
				.param("size", "10")
				.param("searchKeyword", "  "))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].name").value("한꿈출판사"));

		verify(publisherService, times(1)).getPublishers(any(Pageable.class));
	}
}
