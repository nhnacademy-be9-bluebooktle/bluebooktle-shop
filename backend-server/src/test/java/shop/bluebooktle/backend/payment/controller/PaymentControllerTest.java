package shop.bluebooktle.backend.payment.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.auth.service.impl.BackendAuthUserLoaderImpl;
import shop.bluebooktle.backend.payment.service.PaymentService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.payment.request.PaymentCancelRequest;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.exception.handler.GlobalExceptionHandler;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(PaymentController.class)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PaymentService paymentService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private BackendAuthUserLoaderImpl backendAuthUserLoader;

	private PaymentConfirmRequest mockPaymentConfirmRequest;
	private PaymentCancelRequest mockPaymentCancelRequest;
	private UserPrincipal mockUserPrincipal;
	private UserDto mockUserDto;

	@BeforeEach
	void setUp() {
		mockPaymentConfirmRequest = new PaymentConfirmRequest(
			"testPaymentKey",
			"testOrderId123456",
			15000L
		);

		mockPaymentCancelRequest = new PaymentCancelRequest(
			"testOrderKey123",
			"단순 변심"
		);

		mockUserDto = UserDto.builder()
			.id(1L)
			.loginId("user@example.com")
			.nickname("Test Nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		mockUserPrincipal = new UserPrincipal(mockUserDto);
	}

	@Test
	@DisplayName("confirmPayment: 결제 확정 성공")
	void confirmPayment_success() throws Exception {
		String gatewayName = "TOSS";

		doNothing().when(paymentService).confirmPayment(any(PaymentConfirmRequest.class), eq(gatewayName));

		mockMvc.perform(post("/api/payments/{gateway-name}/confirm", gatewayName)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockPaymentConfirmRequest))
				.with(csrf())
				.with(user(mockUserPrincipal)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.status").value("SUCCESS"))
			.andExpect(jsonPath("$.data.orderId").value(mockPaymentConfirmRequest.orderId()))
			.andExpect(jsonPath("$.data.totalAmount").value(mockPaymentConfirmRequest.amount().intValue()));

		verify(paymentService).confirmPayment(any(PaymentConfirmRequest.class), eq(gatewayName));
	}

	@Test
	@DisplayName("confirmPayment: 결제 확정 실패 - paymentKey 누락")
	void confirmPayment_invalidRequest_missingPaymentKey() throws Exception {
		String gatewayName = "TOSS";
		PaymentConfirmRequest invalidRequest = new PaymentConfirmRequest(null, "testOrderId123456", 10000L);

		mockMvc.perform(post("/api/payments/{gateway-name}/confirm", gatewayName)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
				.with(csrf())
				.with(user(mockUserPrincipal)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.paymentKey").value("must not be blank"));
	}

	@Test
	@DisplayName("confirmPayment: 결제 확정 실패 - orderId 크기 초과")
	void confirmPayment_invalidRequest_orderIdTooLong() throws Exception {
		String gatewayName = "TOSS";
		PaymentConfirmRequest invalidRequest = new PaymentConfirmRequest("key123", "a".repeat(65), 10000L);

		mockMvc.perform(post("/api/payments/{gateway-name}/confirm", gatewayName)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
				.with(csrf())
				.with(user(mockUserPrincipal)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.orderId").value("size must be between 6 and 64"));
	}

	@Test
	@DisplayName("confirmPayment: 결제 확정 실패 - 서비스 예외 발생")
	void confirmPayment_serviceException() throws Exception {
		String gatewayName = "TOSS";
		String errorMessage = "결제 확정 중 오류가 발생했습니다.";

		doThrow(new RuntimeException(errorMessage))
			.when(paymentService).confirmPayment(any(PaymentConfirmRequest.class), eq(gatewayName));

		mockMvc.perform(post("/api/payments/{gateway-name}/confirm", gatewayName)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockPaymentConfirmRequest))
				.with(csrf())
				.with(user(mockUserPrincipal)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.message").value("예상치 못한 오류가 발생했습니다. 문제가 지속되면 관리자에게 문의하세요."))
			.andExpect(jsonPath("$.code").value("C001"));

		verify(paymentService).confirmPayment(any(PaymentConfirmRequest.class), eq(gatewayName));
	}

	@Test
	@DisplayName("cancelPayment: 결제 취소 성공 - 인증된 사용자")
	void cancelPayment_success_authenticatedUser() throws Exception {
		doNothing().when(paymentService)
			.cancelPayment(any(PaymentCancelRequest.class), eq(mockUserPrincipal.getUserId()));

		mockMvc.perform(post("/api/payments/cancel")
				.with(user(mockUserPrincipal))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockPaymentCancelRequest))
				.with(csrf())
				.with(user(mockUserPrincipal)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(paymentService).cancelPayment(any(PaymentCancelRequest.class), eq(mockUserPrincipal.getUserId()));
	}

	@Test
	@DisplayName("cancelPayment: 결제 취소 실패 - orderKey 누락")
	void cancelPayment_invalidRequest_missingOrderKey() throws Exception {
		PaymentCancelRequest invalidRequest = new PaymentCancelRequest(null, "취소 사유");

		mockMvc.perform(post("/api/payments/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
				.with(csrf())
				.with(user(mockUserPrincipal)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.orderKey").value("must not be blank"));
	}

	@Test
	@DisplayName("cancelPayment: 결제 취소 실패 - orderKey 크기 초과")
	void cancelPayment_invalidRequest_orderKeyTooLong() throws Exception {
		PaymentCancelRequest invalidRequest = new PaymentCancelRequest("b".repeat(256), "취소 사유");

		mockMvc.perform(post("/api/payments/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
				.with(csrf())
				.with(user(mockUserPrincipal)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.orderKey").value("size must be between 1 and 255"));
	}

	@Test
	@DisplayName("cancelPayment: 결제 취소 실패 - 서비스 예외 발생 (글로벌 핸들러 적용)")
	void cancelPayment_serviceException_globalHandler() throws Exception {
		String errorMessage = "결제 취소 중 오류가 발생했습니다.";
		String expectedResponseMessage = "예상치 못한 오류가 발생했습니다. 문제가 지속되면 관리자에게 문의하세요.";
		String expectedErrorCode = "C001";

		doThrow(new RuntimeException(errorMessage))
			.when(paymentService).cancelPayment(any(PaymentCancelRequest.class), eq(mockUserPrincipal.getUserId()));

		mockMvc.perform(post("/api/payments/cancel")
				.with(user(mockUserPrincipal))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockPaymentCancelRequest))
				.with(csrf())
				.with(user(mockUserPrincipal)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.message").value(expectedResponseMessage))
			.andExpect(jsonPath("$.code").value(expectedErrorCode));

		verify(paymentService).cancelPayment(any(PaymentCancelRequest.class), eq(mockUserPrincipal.getUserId()));
	}
}