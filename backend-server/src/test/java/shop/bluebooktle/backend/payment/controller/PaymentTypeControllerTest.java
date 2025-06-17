package shop.bluebooktle.backend.payment.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentTypeResponse;
import shop.bluebooktle.backend.payment.service.PaymentTypeService;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(PaymentTypeController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class PaymentTypeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PaymentTypeService paymentTypeService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	private PaymentTypeRequest mockPaymentTypeRequest;
	private PaymentTypeResponse mockPaymentTypeResponse;

	@BeforeEach
	void setUp() {
		mockPaymentTypeRequest = new PaymentTypeRequest("Credit Card");
		mockPaymentTypeResponse = new PaymentTypeResponse(1L, "Credit Card");
	}

	@Test
	@DisplayName("getPaymentTypes: 결제 수단 목록 조회 성공")
	void getPaymentTypes_success() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		PageImpl<PaymentTypeResponse> paymentTypePage = new PageImpl<>(List.of(mockPaymentTypeResponse), pageable, 1);

		when(paymentTypeService.getAll(any(Pageable.class))).thenReturn(paymentTypePage);

		mockMvc.perform(get("/api/payments/types")
				.param("page", "0")
				.param("size", "10")
				.param("sort", "id,asc")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(mockPaymentTypeResponse.id()))
			.andExpect(jsonPath("$.data.content[0].method").value(mockPaymentTypeResponse.method()));

		verify(paymentTypeService).getAll(any(Pageable.class));
	}

	@Test
	@DisplayName("getPaymentTypes: 결제 수단이 없을 때 빈 목록 반환")
	void getPaymentTypes_noContent() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		PageImpl<PaymentTypeResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

		when(paymentTypeService.getAll(any(Pageable.class))).thenReturn(emptyPage);

		mockMvc.perform(get("/api/payments/types")
				.param("page", "0")
				.param("size", "10")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content").isEmpty())
			.andExpect(jsonPath("$.data.totalElements").value(0));

		verify(paymentTypeService).getAll(any(Pageable.class));
	}

	@Test
	@DisplayName("registerPaymentType: 결제 수단 등록 성공")
	void registerPaymentType_success() throws Exception {
		doNothing().when(paymentTypeService).create(any(PaymentTypeRequest.class));

		mockMvc.perform(post("/api/payments/types")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockPaymentTypeRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"));

		verify(paymentTypeService).create(any(PaymentTypeRequest.class));
	}

	@Test
	@DisplayName("registerPaymentType: 유효하지 않은 요청으로 결제 수단 등록 실패 (method 누락/빈 값)")
	void registerPaymentType_invalidRequest_missingMethod() throws Exception {
		PaymentTypeRequest invalidRequest = new PaymentTypeRequest("");

		mockMvc.perform(post("/api/payments/types")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.method").value("must not be blank"));
	}

	@Test
	@DisplayName("updatePaymentType: 결제 수단 수정 성공")
	void updatePaymentType_success() throws Exception {
		Long paymentTypeId = 1L;
		doNothing().when(paymentTypeService).update(eq(paymentTypeId), any(PaymentTypeRequest.class));

		mockMvc.perform(put("/api/payments/types/{paymentTypeId}", paymentTypeId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockPaymentTypeRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(paymentTypeService).update(eq(paymentTypeId), any(PaymentTypeRequest.class));
	}

	@Test
	@DisplayName("updatePaymentType: 존재하지 않는 ID로 결제 수단 수정 시 실패")
	void updatePaymentType_notFound() throws Exception {
		Long nonExistentId = 999L;
		doThrow(new RuntimeException("PaymentType not found")).when(paymentTypeService)
			.update(eq(nonExistentId), any(PaymentTypeRequest.class));

		mockMvc.perform(put("/api/payments/types/{paymentTypeId}", nonExistentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockPaymentTypeRequest)))
			.andExpect(status().isInternalServerError());
	}

	@Test
	@DisplayName("deletePaymentType: 결제 수단 삭제 성공")
	void deletePaymentType_success() throws Exception {
		Long paymentTypeId = 1L;
		doNothing().when(paymentTypeService).delete(paymentTypeId);

		mockMvc.perform(delete("/api/payments/types/{paymentTypeId}", paymentTypeId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(paymentTypeService).delete(paymentTypeId);
	}

	@Test
	@DisplayName("deletePaymentType: 존재하지 않는 ID로 결제 수단 삭제 시 실패")
	void deletePaymentType_notFound() throws Exception {
		Long nonExistentId = 999L;
		doThrow(new RuntimeException("PaymentType not found")).when(paymentTypeService)
			.delete(nonExistentId);

		mockMvc.perform(delete("/api/payments/types/{paymentTypeId}", nonExistentId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError());
	}
}