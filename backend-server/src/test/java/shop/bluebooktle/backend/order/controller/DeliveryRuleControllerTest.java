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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.service.DeliveryRuleService;

@WebMvcTest(controllers = DeliveryRuleController.class)
class DeliveryRuleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DeliveryRuleService deliveryRuleService;

	@Autowired
	private ObjectMapper objectMapper;

	// @Test
	// @DisplayName("배송 정책 등록 - 성공")
	// @WithMockUser
	// void createDeliveryPolicy_success() throws Exception {
	// 	DeliveryRuleRequest request = new DeliveryRuleRequest("기본 배송", new BigDecimal("30000"), new BigDecimal("3000"));
	//
	// 	DeliveryRule saved = DeliveryRule.builder()
	// 		.name(request.name())
	// 		.price(request.price())
	// 		.deliveryFee(request.deliveryFee())
	// 		.build();
	// 	given(deliveryRuleService.createPolicy(anyString(), any(), any())).willReturn(saved);
	//
	// 	mockMvc.perform(post("/api/admin/delivery-rules")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(request)))
	// 		.andExpect(status().isCreated())
	// 		.andExpect(jsonPath("$.status").value("success"));
	//
	// 	verify(deliveryRuleService).createPolicy(anyString(), any(), any());
	// }

	@Test
	@DisplayName("단일 배송 정책 조회 - 성공")
	@WithMockUser
	void getRule_success() throws Exception {
		Long id = 1L;
		DeliveryRule rule = DeliveryRule.builder()
			.name("기본 배송")
			.price(new BigDecimal("30000"))
			.deliveryFee(new BigDecimal("3000"))
			.build();

		given(deliveryRuleService.getRule(id)).willReturn(rule);

		mockMvc.perform(get("/api/admin/delivery-rules/{id}", id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.name").value("기본 배송"));

		verify(deliveryRuleService).getRule(id);
	}

	@Test
	@DisplayName("전체 배송 정책 조회 - 성공")
	@WithMockUser
	void getAllRules_success() throws Exception {
		List<DeliveryRule> rules = List.of(
			DeliveryRule.builder()
				.name("기본 배송")
				.price(new BigDecimal("30000"))
				.deliveryFee(new BigDecimal("3000"))
				.build()
		);

		given(deliveryRuleService.getAll()).willReturn(rules);

		mockMvc.perform(get("/api/admin/delivery-rules/all"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray());

		verify(deliveryRuleService).getAll();
	}

	// @Test
	// @DisplayName("배송 정책 삭제 - 성공")
	// @WithMockUser
	// void deleteRule_success() throws Exception {
	// 	Long id = 1L;
	// 	mockMvc.perform(delete("/api/admin/delivery-rules/{id}", id))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"));
	//
	// 	verify(deliveryRuleService).deletePolicy(id);
	// }
}
