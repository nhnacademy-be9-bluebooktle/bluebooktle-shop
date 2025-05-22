package shop.bluebooktle.backend.book_order.controller;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book_order.service.PackagingOptionService;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.service.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(PackagingOptionController.class)
class PackagingOptionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PackagingOptionService packagingOptionService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("포장 옵션 등록 - 성공")
	@WithMockUser
	void createPackagingOption_success() throws Exception {
		PackagingOptionRequest request = PackagingOptionRequest.builder()
			.name("기본 포장지")
			.price(BigDecimal.valueOf(1500))
			.build();

		PackagingOptionInfoResponse response = PackagingOptionInfoResponse.builder()
			.packagingOptionId(1L)
			.name("기본 포장지")
			.price(BigDecimal.valueOf(1500))
			.build();

		given(packagingOptionService.createPackagingOption(request)).willReturn(response);

		mockMvc.perform(post("/api/options")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)).with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.name").value("기본 포장지"));
	}

	@Test
	@DisplayName("포장 옵션 전체 조회 - 성공")
	@WithMockUser
	void getPackagingOptions_success() throws Exception {
		// given
		PackagingOptionInfoResponse option1 = PackagingOptionInfoResponse.builder()
			.packagingOptionId(1L)
			.name("기본 포장지")
			.price(BigDecimal.valueOf(1000))
			.build();

		PackagingOptionInfoResponse option2 = PackagingOptionInfoResponse.builder()
			.packagingOptionId(2L)
			.name("고급 포장지")
			.price(BigDecimal.valueOf(3000))
			.build();

		Page<PackagingOptionInfoResponse> page = new PageImpl<>(List.of(option1, option2));

		given(packagingOptionService.getPackagingOption(any(Pageable.class)))
			.willReturn(page);

		// when & then
		mockMvc.perform(get("/api/options")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].name").value("기본 포장지"))
			.andExpect(jsonPath("$.data.content[1].name").value("고급 포장지"));
	}

	// @Test
	// @DisplayName("포장 옵션 수정 - 성공")
	// @WithMockUser
	// void updatePackagingOption_success() throws Exception {
	// 	PackagingOptionUpdateRequest request = PackagingOptionUpdateRequest.builder()
	// 		.name("보라 포장지")
	// 		.price(BigDecimal.valueOf(2500))
	// 		.build();
	//
	// 	PackagingOptionResponse response = PackagingOptionResponse.builder()
	// 		.packagingOptionId(1L)
	// 		.name("보라 포장지")
	// 		.price(BigDecimal.valueOf(2500))
	// 		.build();
	//
	// 	given(packagingOptionService.updatePackagingOption(1L, request)).willReturn(response);
	//
	// 	mockMvc.perform(put("/api/options/1")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(request)))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data.name").value("보라 포장지"));
	// }

	// @Test
	// @DisplayName("포장 옵션 삭제 - 성공")
	// @WithMockUser
	// void deletePackagingOption_success() throws Exception {
	// 	doNothing().when(packagingOptionService).deletePackagingOption(1L);
	//
	// 	mockMvc.perform(delete("/api/options/1"))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"));
	// }
}
