package shop.bluebooktle.backend.book_order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book_order.service.PackagingOptionService;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(controllers = PackagingOptionController.class)
@WithMockUser
@ActiveProfiles("test")
public class PackagingOptionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PackagingOptionService packagingOptionService;

	// Spring Security 필터 빈 모킹
	@MockitoBean
	private JwtUtil jwtUtil;
	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("포장 옵션 등록 - 성공")
	void createPackagingOption_success() throws Exception {
		// given
		PackagingOptionRequest req = PackagingOptionRequest.builder()
			.name("땡땡이 포장지")
			.price(BigDecimal.valueOf(500))
			.build();
		PackagingOptionInfoResponse respDto =
			PackagingOptionInfoResponse.builder()
				.id(1L)
				.name("땡땡이 포장지")
				.price(BigDecimal.valueOf(500))
				.build();

		given(packagingOptionService.createPackagingOption(any(PackagingOptionRequest.class)))
			.willReturn(respDto);

		// when & then
		mockMvc.perform(post("/api/options")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.name").value("땡땡이 포장지"));

		then(packagingOptionService).should()
			.createPackagingOption(any(PackagingOptionRequest.class));
	}

	@Test
	@DisplayName("전체 조회 - 성공")
	void getPackagingOptions_noKeyword() throws Exception {
		// given
		PackagingOptionInfoResponse dto =
			new PackagingOptionInfoResponse(2L, "Box", BigDecimal.valueOf(300));
		given(packagingOptionService.getPackagingOptions(any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(dto)));

		// when & then
		mockMvc.perform(get("/api/options")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(2));

		then(packagingOptionService).should()
			.getPackagingOptions(any(Pageable.class));
	}

	@Test
	@DisplayName("검색 조회 - 성공")
	void getPackagingOptions_withKeyword() throws Exception {
		// given
		String keyword = "선물";
		PackagingOptionInfoResponse dto =
			new PackagingOptionInfoResponse(3L, "선물 상자", BigDecimal.valueOf(1000));
		given(packagingOptionService.searchPackagingOption(eq(keyword), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(dto)));

		// when & then
		mockMvc.perform(get("/api/options")
				.param("searchKeyword", keyword)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].name").value("선물 상자"));

		then(packagingOptionService).should()
			.searchPackagingOption(eq(keyword), any(Pageable.class));
	}

	@DisplayName("검색 키워드가 null일 때 전체 조회 - 성공")
	@Test
	void getPackagingOptions_withNullKeyword() throws Exception {
		PackagingOptionInfoResponse dto = new PackagingOptionInfoResponse(1L, "기본 포장지", BigDecimal.valueOf(500));
		given(packagingOptionService.getPackagingOptions(any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(dto)));

		mockMvc.perform(get("/api/options")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(1));

		then(packagingOptionService).should()
			.getPackagingOptions(any(Pageable.class));
	}

	@Test
	@DisplayName("검색 키워드가 빈 문자열일 때 전체 조회 - 성공")
	void getPackagingOptions_withEmptyKeyword() throws Exception {
		PackagingOptionInfoResponse dto =
			new PackagingOptionInfoResponse(7L, "포장지", BigDecimal.valueOf(300));
		given(packagingOptionService.getPackagingOptions(any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(dto)));

		mockMvc.perform(get("/api/options")
				.param("searchKeyword", " ")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(7));

		then(packagingOptionService).should()
			.getPackagingOptions(any(Pageable.class));
	}

	@Test
	@DisplayName("단건 조회 - 성공")
	void getPackagingOption_success() throws Exception {
		// given
		PackagingOptionInfoResponse dto =
			new PackagingOptionInfoResponse(4L, "리본 포장지", BigDecimal.valueOf(200));
		given(packagingOptionService.getPackagingOption(4L)).willReturn(dto);

		// when & then
		mockMvc.perform(get("/api/options/4")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(4))
			.andExpect(jsonPath("$.data.name").value("리본 포장지"));

		then(packagingOptionService).should().getPackagingOption(4L);
	}

	@Test
	@DisplayName("수정 - 성공")
	void updatePackagingOption_success() throws Exception {
		// given
		PackagingOptionRequest req =
			PackagingOptionRequest.builder()
				.name("체크 포장지")
				.price(BigDecimal.valueOf(800))
				.build();
		PackagingOptionInfoResponse dto =
			new PackagingOptionInfoResponse(5L, "체크 포장지", BigDecimal.valueOf(800));
		given(packagingOptionService.updatePackagingOption(eq(5L), any(PackagingOptionRequest.class)))
			.willReturn(dto);

		// when & then
		mockMvc.perform(put("/api/options/5")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.name").value("체크 포장지"));

		then(packagingOptionService).should()
			.updatePackagingOption(eq(5L), any(PackagingOptionRequest.class));
	}

	@Test
	@DisplayName("삭제 - 성공")
	void deletePackagingOption_success() throws Exception {
		// given
		willDoNothing().given(packagingOptionService).deletePackagingOption(6L);

		// when & then
		mockMvc.perform(delete("/api/options/6")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		then(packagingOptionService).should().deletePackagingOption(6L);
	}
}
