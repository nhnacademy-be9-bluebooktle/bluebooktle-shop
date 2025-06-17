package shop.bluebooktle.backend.order.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.order.service.DeliveryRuleService;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@ActiveProfiles("test")
@WebMvcTest(controllers = DeliveryRuleController.class)
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
	@DisplayName("활성 배송 정책 조회 - 성공")
	@WithMockUser(roles = "USER")
	void getDeliveryRules_success() throws Exception {
		DeliveryRuleResponse activeRule = new DeliveryRuleResponse(
			1L,
			"기본 배송",
			new BigDecimal("3000"),
			new BigDecimal("30000"),
			Region.ALL,
			true
		);
		given(deliveryRuleService.getAllByIsActive()).willReturn(List.of(activeRule));

		mockMvc.perform(get("/api/delivery-rules"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].ruleName").value("기본 배송"));

		verify(deliveryRuleService).getAllByIsActive();
	}

	@Test
	@DisplayName("기본 배송 정책 조회 - 성공")
	@WithMockUser(roles = "USER")
	void getDefaultDeliveryRule_success() throws Exception {
		DeliveryRuleResponse defaultRule = new DeliveryRuleResponse(
			2L,
			"기본값 배송",
			new BigDecimal("2500"),
			new BigDecimal("20000"),
			Region.ALL,
			true
		);
		given(deliveryRuleService.getDefaultRule()).willReturn(defaultRule);

		mockMvc.perform(get("/api/delivery-rules/default"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.ruleName").value("기본값 배송"))
			.andExpect(jsonPath("$.data.id").value(2));

		verify(deliveryRuleService).getDefaultRule();
	}
}