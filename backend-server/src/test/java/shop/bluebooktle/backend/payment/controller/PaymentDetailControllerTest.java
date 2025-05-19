package shop.bluebooktle.backend.payment.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.payment.dto.request.PaymentDetailRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentDetailResponse;
import shop.bluebooktle.backend.payment.service.PaymentDetailService;
import shop.bluebooktle.common.domain.payment.PaymentStatus;

@WebMvcTest(controllers = PaymentDetailController.class)
class PaymentDetailControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PaymentDetailService service;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("결제 상세 생성 - 성공")
	void createPaymentDetail_success() throws Exception {
		// given: 유효한 요청 객체
		PaymentDetailRequest req = new PaymentDetailRequest(
			1L,                 // paymentId
			2L,                 // paymentTypeId
			"abc123",           // paymentKey
			PaymentStatus.DONE // paymentStatus
		);

		// when & then
		mockMvc.perform(post("/api/payments/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		then(service).should().create(any(PaymentDetailRequest.class));
	}

	@Test
	@DisplayName("결제 상세 삭제 - 성공")
	void deletePaymentDetail_success() throws Exception {
		mockMvc.perform(delete("/api/payments/details/{id}", 42L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		then(service).should().delete(42L);
	}

	@Test
	@DisplayName("결제 상세 조회 - 성공")
	void getPaymentDetail_success() throws Exception {
		// given: 서비스가 반환할 더미 응답
		PaymentDetailResponse dto = new PaymentDetailResponse(
			99L,                // id
			2L,                 // paymentTypeId
			"xyz789",           // paymentKey
			PaymentStatus.DONE // paymentStatus
		);
		given(service.get(99L)).willReturn(dto);

		// when & then
		mockMvc.perform(get("/api/payments/details/{id}", 99L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(99))
			.andExpect(jsonPath("$.data.paymentTypeId").value(2))
			.andExpect(jsonPath("$.data.paymentKey").value("xyz789"))
			.andExpect(jsonPath("$.data.paymentStatus").value("DONE"));

		then(service).should().get(99L);
	}
}
