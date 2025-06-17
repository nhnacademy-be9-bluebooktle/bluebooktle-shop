package shop.bluebooktle.backend.payment.gateway;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import shop.bluebooktle.backend.payment.client.TossPaymentClient;
import shop.bluebooktle.backend.payment.dto.request.GenericPaymentCancelRequest;
import shop.bluebooktle.backend.payment.dto.request.TossApiPaymentCancelRequest;
import shop.bluebooktle.backend.payment.dto.request.TossApiPaymentConfirmRequest;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentCancelResponse;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentConfirmResponse;
import shop.bluebooktle.backend.payment.dto.response.PaymentStatus;
import shop.bluebooktle.backend.payment.dto.response.TossApiPaymentCancelSuccessResponse;
import shop.bluebooktle.backend.payment.dto.response.TossApiPaymentConfirmSuccessResponse;
import shop.bluebooktle.backend.payment.gateway.impl.TossPaymentGatewayImpl;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;

@ExtendWith(MockitoExtension.class)
class TossPaymentGatewayTest {

	@InjectMocks
	private TossPaymentGatewayImpl tossPaymentGateway;

	@Mock
	private TossPaymentClient tossPaymentClient;

	private String mockSecretKey = "test_secret_key";
	private String encodedAuthHeader;

	private PaymentConfirmRequest mockCommonConfirmRequest;
	private TossApiPaymentConfirmSuccessResponse mockTossConfirmSuccessResponse;

	private GenericPaymentCancelRequest mockCommonCancelRequest;
	private TossApiPaymentCancelSuccessResponse mockTossCancelSuccessResponse;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(tossPaymentGateway, "secretKey", mockSecretKey);
		encodedAuthHeader = "Basic " + java.util.Base64.getEncoder().encodeToString((mockSecretKey + ":").getBytes());

		mockCommonConfirmRequest = new PaymentConfirmRequest(
			"testPaymentKey123",
			"testOrderId456",
			15000L
		);
		mockTossConfirmSuccessResponse = new TossApiPaymentConfirmSuccessResponse(
			"testPaymentKey123",
			"testOrderId456",
			"DONE",
			15000L,
			"카드"
		);

		mockCommonCancelRequest = new GenericPaymentCancelRequest(
			"testPaymentKey123",
			"고객 변심"
		);

		TossApiPaymentCancelSuccessResponse.Cancel mockTossApiCancel = new TossApiPaymentCancelSuccessResponse.Cancel(
			BigDecimal.valueOf(10000),
			"고객 변심",
			BigDecimal.valueOf(0),
			BigDecimal.valueOf(0),
			BigDecimal.valueOf(5000),
			OffsetDateTime.now(),
			"cancelTxKey789",
			"cancelReceiptKey101",
			"DONE"
		);

		mockTossCancelSuccessResponse = new TossApiPaymentCancelSuccessResponse(
			"mId_test",
			"v1.0",
			"testPaymentKey123",
			"testOrderId456",
			"테스트 주문",
			"KRW",
			"카드",
			BigDecimal.valueOf(15000),
			BigDecimal.valueOf(5000),
			"CANCELED",
			OffsetDateTime.now().minusHours(1),
			OffsetDateTime.now(),
			false,
			"lastTxKey999",
			BigDecimal.valueOf(13636),
			BigDecimal.valueOf(1364),
			BigDecimal.valueOf(0),
			true,
			List.of(mockTossApiCancel),
			null,
			"KR",
			null,
			null
		);
	}

	@Test
	@DisplayName("getGatewayName: 올바른 게이트웨이 이름 반환")
	void getGatewayName_returnsCorrectName() {
		String gatewayName = tossPaymentGateway.getGatewayName();
		assertThat(gatewayName).isEqualTo("TOSS");
	}

	@Test
	@DisplayName("confirmPayment: 결제 확인 성공 - DONE 상태")
	void confirmPayment_success_doneStatus() {
		when(tossPaymentClient.confirmPayment(eq(encodedAuthHeader), any(TossApiPaymentConfirmRequest.class)))
			.thenReturn(mockTossConfirmSuccessResponse);

		GenericPaymentConfirmResponse response = tossPaymentGateway.confirmPayment(mockCommonConfirmRequest);

		assertThat(response).isNotNull();
		assertThat(response.transactionId()).isEqualTo(mockTossConfirmSuccessResponse.paymentKey());
		assertThat(response.orderId()).isEqualTo(mockTossConfirmSuccessResponse.orderId());
		assertThat(response.confirmedAmount()).isEqualTo(
			BigDecimal.valueOf(mockTossConfirmSuccessResponse.totalAmount()));
		assertThat(response.status()).isEqualTo(PaymentStatus.SUCCESS);
		assertThat(response.paymentMethodDetail()).isEqualTo(mockTossConfirmSuccessResponse.method());
		assertThat(response.additionalData()).containsEntry("tossPaymentKey",
			mockTossConfirmSuccessResponse.paymentKey());
		assertThat(response.additionalData()).containsEntry("tossPaymentStatus",
			mockTossConfirmSuccessResponse.status());

		verify(tossPaymentClient).confirmPayment(eq(encodedAuthHeader), any(TossApiPaymentConfirmRequest.class));
	}

	@Test
	@DisplayName("confirmPayment: 결제 확인 성공 - DONE 이외 상태 (FAILURE로 처리)")
	void confirmPayment_success_nonDoneStatus() {
		TossApiPaymentConfirmSuccessResponse nonDoneResponse = new TossApiPaymentConfirmSuccessResponse(
			"testPaymentKeyNonDone",
			"testOrderIdNonDone",
			"CANCELED",
			10000L,
			"가상계좌"
		);
		when(tossPaymentClient.confirmPayment(eq(encodedAuthHeader), any(TossApiPaymentConfirmRequest.class)))
			.thenReturn(nonDoneResponse);

		GenericPaymentConfirmResponse response = tossPaymentGateway.confirmPayment(mockCommonConfirmRequest);

		assertThat(response).isNotNull();
		assertThat(response.status()).isEqualTo(PaymentStatus.FAILURE);
		assertThat(response.confirmedAmount()).isEqualTo(BigDecimal.valueOf(nonDoneResponse.totalAmount()));
		assertThat(response.paymentMethodDetail()).isEqualTo(nonDoneResponse.method());
		assertThat(response.additionalData()).containsEntry("tossPaymentStatus", "CANCELED");
	}

	@Test
	@DisplayName("confirmPayment: FeignException 발생 시 실패 응답 반환")
	void confirmPayment_feignException() {
		int errorStatus = 400;
		String errorMessage = "{\"code\":\"INVALID_REQUEST\",\"message\":\"유효하지 않은 요청입니다.\"}";

		Request request = Request.create(
			Request.HttpMethod.POST,
			"/confirm",
			Collections.emptyMap(),
			errorMessage.getBytes(StandardCharsets.UTF_8),
			StandardCharsets.UTF_8,
			new RequestTemplate()
		);

		Response response = Response.builder()
			.status(errorStatus)
			.request(request)
			.body(new ByteArrayInputStream(errorMessage.getBytes(StandardCharsets.UTF_8)),
				errorStatus)
			.headers(Collections.emptyMap())
			.build();

		FeignException feignException = FeignException.errorStatus("confirmPayment", response);

		when(tossPaymentClient.confirmPayment(eq(encodedAuthHeader), any(TossApiPaymentConfirmRequest.class)))
			.thenThrow(feignException);

		GenericPaymentConfirmResponse actualResponse = tossPaymentGateway.confirmPayment(mockCommonConfirmRequest);

		assertThat(actualResponse).isNotNull();
		assertThat(actualResponse.transactionId()).isNull();
		assertThat(actualResponse.orderId()).isEqualTo(mockCommonConfirmRequest.orderId());
		assertThat(actualResponse.confirmedAmount()).isEqualTo(BigDecimal.valueOf(mockCommonConfirmRequest.amount()));
		assertThat(actualResponse.status()).isEqualTo(PaymentStatus.FAILURE);
		assertThat(actualResponse.paymentMethodDetail()).isEqualTo("TOSS_API_ERROR");
		assertThat(actualResponse.additionalData()).containsEntry("tossApiErrorStatus", errorStatus);
		assertThat(actualResponse.additionalData()).containsEntry("tossApiErrorMessage", errorMessage);

		verify(tossPaymentClient).confirmPayment(eq(encodedAuthHeader), any(TossApiPaymentConfirmRequest.class));
	}

	@Test
	@DisplayName("confirmPayment: 일반 Exception 발생 시 실패 응답 반환")
	void confirmPayment_generalException() {
		String exceptionMessage = "네트워크 연결 불안정";
		when(tossPaymentClient.confirmPayment(eq(encodedAuthHeader), any(TossApiPaymentConfirmRequest.class)))
			.thenThrow(new RuntimeException(exceptionMessage));

		GenericPaymentConfirmResponse response = tossPaymentGateway.confirmPayment(mockCommonConfirmRequest);

		assertThat(response).isNotNull();
		assertThat(response.transactionId()).isNull();
		assertThat(response.orderId()).isEqualTo(mockCommonConfirmRequest.orderId());
		assertThat(response.confirmedAmount()).isEqualTo(BigDecimal.valueOf(mockCommonConfirmRequest.amount()));
		assertThat(response.status()).isEqualTo(PaymentStatus.FAILURE);
		assertThat(response.paymentMethodDetail()).isEqualTo("UNKNOWN_ERROR");
		assertThat(response.additionalData()).containsEntry("errorMessage", exceptionMessage);

		verify(tossPaymentClient).confirmPayment(eq(encodedAuthHeader), any(TossApiPaymentConfirmRequest.class));
	}

	@Test
	@DisplayName("cancelPayment: 결제 취소 성공")
	void cancelPayment_success() {
		when(tossPaymentClient.cancelPayment(eq(encodedAuthHeader), eq(mockCommonCancelRequest.paymentKey()),
			any(TossApiPaymentCancelRequest.class)))
			.thenReturn(mockTossCancelSuccessResponse);

		GenericPaymentCancelResponse response = tossPaymentGateway.cancelPayment(mockCommonCancelRequest);

		assertThat(response).isNotNull();
		assertThat(response.transactionId()).isEqualTo(
			mockTossCancelSuccessResponse.cancels().getFirst().transactionKey());
		assertThat(response.orderId()).isEqualTo(mockTossCancelSuccessResponse.orderId());
		assertThat(response.canceledAmount()).isEqualTo(
			mockTossCancelSuccessResponse.cancels().getFirst().cancelAmount());
		assertThat(response.status()).isEqualTo(PaymentStatus.SUCCESS);
		assertThat(response.paymentMethodDetail()).isEqualTo(mockTossCancelSuccessResponse.method());
		assertThat(response.additionalData()).containsEntry("tossPaymentKey",
			mockTossCancelSuccessResponse.paymentKey());
		assertThat(response.additionalData()).containsEntry("tossCancelStatus", mockTossCancelSuccessResponse.status());
		assertThat(response.additionalData()).containsKey("cancels");
		assertThat((List)response.additionalData().get("cancels")).hasSize(1);

		verify(tossPaymentClient).cancelPayment(eq(encodedAuthHeader), eq(mockCommonCancelRequest.paymentKey()),
			any(TossApiPaymentCancelRequest.class));
	}

	@Test
	@DisplayName("cancelPayment: FeignException 발생 시 실패 응답 반환")
	void cancelPayment_feignException() {
		int errorStatus = 400;
		String errorMessage = "{\"code\":\"NOT_AUTHORIZED\",\"message\":\"권한이 없습니다.\"}";

		Request request = Request.create(
			Request.HttpMethod.POST,
			"/cancel",
			Collections.emptyMap(),
			errorMessage.getBytes(StandardCharsets.UTF_8),
			StandardCharsets.UTF_8,
			new RequestTemplate()
		);

		Response response = Response.builder()
			.status(errorStatus)
			.request(request)
			.body(new ByteArrayInputStream(errorMessage.getBytes(StandardCharsets.UTF_8)), errorStatus)
			.headers(Collections.emptyMap())
			.build();

		FeignException feignException = FeignException.errorStatus("cancelPayment", response);

		when(tossPaymentClient.cancelPayment(eq(encodedAuthHeader), eq(mockCommonCancelRequest.paymentKey()),
			any(TossApiPaymentCancelRequest.class)))
			.thenThrow(feignException);

		GenericPaymentCancelResponse actualResponse = tossPaymentGateway.cancelPayment(mockCommonCancelRequest);

		assertThat(actualResponse).isNotNull();
		assertThat(actualResponse.transactionId()).isNull();
		assertThat(actualResponse.orderId()).isNull();
		assertThat(actualResponse.canceledAmount()).isNull();
		assertThat(actualResponse.status()).isEqualTo(PaymentStatus.FAILURE);
		assertThat(actualResponse.paymentMethodDetail()).isEqualTo("TOSS_API_ERROR");
		assertThat(actualResponse.additionalData()).containsEntry("tossApiErrorStatus", errorStatus);
		assertThat(actualResponse.additionalData()).containsEntry("tossApiErrorMessage", errorMessage);

		verify(tossPaymentClient).cancelPayment(eq(encodedAuthHeader), eq(mockCommonCancelRequest.paymentKey()),
			any(TossApiPaymentCancelRequest.class));
	}

	@Test
	@DisplayName("cancelPayment: 일반 Exception 발생 시 실패 응답 반환")
	void cancelPayment_generalException() {
		String exceptionMessage = "DB 연결 오류";
		when(tossPaymentClient.cancelPayment(eq(encodedAuthHeader), eq(mockCommonCancelRequest.paymentKey()),
			any(TossApiPaymentCancelRequest.class)))
			.thenThrow(new RuntimeException(exceptionMessage));

		GenericPaymentCancelResponse response = tossPaymentGateway.cancelPayment(mockCommonCancelRequest);

		assertThat(response).isNotNull();
		assertThat(response.transactionId()).isNull();
		assertThat(response.orderId()).isNull();
		assertThat(response.canceledAmount()).isNull();
		assertThat(response.status()).isEqualTo(PaymentStatus.FAILURE);
		assertThat(response.paymentMethodDetail()).isEqualTo("UNKNOWN_ERROR");
		assertThat(response.additionalData()).containsEntry("errorMessage", exceptionMessage);

		verify(tossPaymentClient).cancelPayment(eq(encodedAuthHeader), eq(mockCommonCancelRequest.paymentKey()),
			any(TossApiPaymentCancelRequest.class));
	}
}