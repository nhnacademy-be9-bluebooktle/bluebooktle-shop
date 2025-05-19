package shop.bluebooktle.backend.payment.controller;

import static org.mockito.BDDMockito.*;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentTypeResponse;
import shop.bluebooktle.backend.payment.service.PaymentTypeService;

@WebMvcTest(PaymentTypeController.class)
class PaymentTypeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PaymentTypeService service;

	@Test
	@DisplayName("목록 조회 성공")
	void getPaymentTypes_success() throws Exception {
		PaymentTypeResponse dto = new PaymentTypeResponse(1L, "CARD");
		Pageable pageable = PageRequest.of(0, 10);
		List<PaymentTypeResponse> content = List.of(dto);
		Page<PaymentTypeResponse> page = new PageImpl<>(content, pageable, 0);

		given(service.getAll(any())).willReturn(page);

		mockMvc.perform(get("/api/payments/types")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(1))
			.andExpect(jsonPath("$.data.content[0].method").value("CARD"));
	}

	@Test
	@DisplayName("등록 성공")
	void registerPaymentType_success() throws Exception {
		PaymentTypeRequest req = new PaymentTypeRequest("CARD");

		mockMvc.perform(post("/api/payments/types")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		then(service).should().create(req);
	}

	@Test
	@DisplayName("수정 성공")
	void updatePaymentType_success() throws Exception {
		PaymentTypeRequest req = new PaymentTypeRequest("ACCOUNT");

		mockMvc.perform(put("/api/payments/types/{id}", 7L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		then(service).should().update(7L, req);
	}

	@Test
	@DisplayName("삭제 성공")
	void deletePaymentType_success() throws Exception {
		mockMvc.perform(delete("/api/payments/types/{id}", 9L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		then(service).should().delete(9L);
	}
}
