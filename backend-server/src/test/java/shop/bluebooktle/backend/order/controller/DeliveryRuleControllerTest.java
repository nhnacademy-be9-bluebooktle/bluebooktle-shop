package shop.bluebooktle.backend.order.controller;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.order.service.DeliveryRuleService;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(controllers = DeliveryRuleController.class)
@ActiveProfiles("test")
class DeliveryRuleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DeliveryRuleService deliveryRuleService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("배송 정책 등록 - 성공")
	@WithMockUser
	void createDeliveryPolicy_success() throws Exception {
		DeliveryRuleCreateRequest request = new DeliveryRuleCreateRequest(
			"기본 배송",
			new BigDecimal("30000"),
			new BigDecimal("3000"),
			Region.ALL,
			true
		);
		given(deliveryRuleService.createRule(any(DeliveryRuleCreateRequest.class)))
			.willReturn(1L);

		mockMvc.perform(post("/api/admin/delivery-rules")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value(1));

		verify(deliveryRuleService).createRule(any(DeliveryRuleCreateRequest.class));
	}

	@Test
	@DisplayName("단일 배송 정책 조회 - 성공")
	@WithMockUser
	void getRule_success() throws Exception {
		Long id = 1L;
		DeliveryRuleResponse dto = new DeliveryRuleResponse(
			id,
			"기본 배송",
			new BigDecimal("30000"),
			new BigDecimal("3000"),
			Region.ALL,
			true
		);
		given(deliveryRuleService.getRule(id)).willReturn(dto);

		mockMvc.perform(get("/api/admin/delivery-rules/{id}", id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.ruleName").value("기본 배송"))
			.andExpect(jsonPath("$.data.id").value(1));

		verify(deliveryRuleService).getRule(id);
	}

	@Test
	@DisplayName("전체 배송 정책 조회 - 성공")
	@WithMockUser
	void getAllRules_success() throws Exception {
		DeliveryRuleResponse dto = new DeliveryRuleResponse(
			1L,
			"기본 배송",
			new BigDecimal("30000"),
			new BigDecimal("3000"),
			Region.ALL,
			true
		);
		Page<DeliveryRuleResponse> page = new PageImpl<>(List.of(dto));
		given(deliveryRuleService.getAll(any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/api/admin/delivery-rules")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content[0].ruleName").value("기본 배송"));

		verify(deliveryRuleService).getAll(any(Pageable.class));
	}

	@Test
	@DisplayName("배송 정책 삭제 - 성공")
	@WithMockUser
	void deleteRule_success() throws Exception {
		Long id = 1L;

		mockMvc.perform(delete("/api/admin/delivery-rules/{id}", id)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(deliveryRuleService).deleteRule(id);
	}
}
