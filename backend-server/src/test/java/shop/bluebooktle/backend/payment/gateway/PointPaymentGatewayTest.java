package shop.bluebooktle.backend.payment.gateway;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.payment.dto.request.GenericPaymentCancelRequest;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentCancelResponse;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentConfirmResponse;
import shop.bluebooktle.backend.payment.dto.response.PaymentStatus;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.gateway.impl.PointPaymentGatewayImpl;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PointPaymentGatewayTest {

	@InjectMocks
	private PointPaymentGatewayImpl pointPaymentGateway;

	@Mock
	private OrderRepository orderRepository;

	private PaymentConfirmRequest mockPaymentConfirmRequest;
	private GenericPaymentCancelRequest mockGenericPaymentCancelRequest;
	private Order mockOrder;
	private Payment mockPayment;

	@BeforeEach
	void setUp() {
		mockPaymentConfirmRequest = new PaymentConfirmRequest(
			"PAYMENT_KEY_456",
			"ORDER_ID_123",
			10000L
		);

		mockGenericPaymentCancelRequest = new GenericPaymentCancelRequest(
			"ORDER_KEY_789",
			"포인트 결제 취소 사유"
		);

		mockOrder = mock(Order.class);
		mockPayment = mock(Payment.class);

		when(mockOrder.getOrderKey()).thenReturn("ORDER_KEY_789");
		when(mockOrder.getPayment()).thenReturn(mockPayment);
		when(mockPayment.getPaidAmount()).thenReturn(BigDecimal.valueOf(10000));
	}

	@Test
	@DisplayName("getGatewayName: 올바른 게이트웨이 이름 반환")
	void getGatewayName_returnsCorrectName() {
		String gatewayName = pointPaymentGateway.getGatewayName();

		assertThat(gatewayName).isEqualTo("POINT");
	}

	@Test
	@DisplayName("confirmPayment: 성공적인 포인트 결제 확인")
	void confirmPayment_success() {
		GenericPaymentConfirmResponse response = pointPaymentGateway.confirmPayment(mockPaymentConfirmRequest);

		assertThat(response).isNotNull();
		assertThat(response.transactionId()).isEqualTo(mockPaymentConfirmRequest.orderId());
		assertThat(response.orderId()).isEqualTo(mockPaymentConfirmRequest.orderId());
		assertThat(response.confirmedAmount()).isEqualTo(BigDecimal.valueOf(mockPaymentConfirmRequest.amount()));
		assertThat(response.status()).isEqualTo(PaymentStatus.SUCCESS);
		assertThat(response.paymentMethodDetail()).isEqualTo("POINT_포인트 결제");
		assertThat(response.additionalData()).isNull();
	}

	@Test
	@DisplayName("confirmPayment: 결제 확인 중 예외 발생 시 실패 응답 반환 (amount가 0L)")
	void confirmPayment_exception() {
		PaymentConfirmRequest validRequestWithZeroAmount = new PaymentConfirmRequest("validKey", "validOrderId", 0L);

		GenericPaymentConfirmResponse response = pointPaymentGateway.confirmPayment(validRequestWithZeroAmount);

		assertThat(response).isNotNull();
		assertThat(response.status()).isEqualTo(PaymentStatus.SUCCESS);
		assertThat(response.paymentMethodDetail()).isEqualTo("POINT_포인트 결제");
		assertThat(response.additionalData()).isNull();
	}

	@Test
	@DisplayName("cancelPayment: 성공적인 포인트 결제 취소")
	void cancelPayment_success() {
		when(orderRepository.findByOrderKey(anyString())).thenReturn(Optional.of(mockOrder));

		GenericPaymentCancelResponse response = pointPaymentGateway.cancelPayment(mockGenericPaymentCancelRequest);

		assertThat(response).isNotNull();
		assertThat(response.transactionId()).isEqualTo(mockOrder.getOrderKey());
		assertThat(response.orderId()).isEqualTo(mockOrder.getOrderKey());
		assertThat(response.canceledAmount()).isEqualTo(mockPayment.getPaidAmount());
		assertThat(response.status()).isEqualTo(PaymentStatus.SUCCESS);
		assertThat(response.paymentMethodDetail()).isEqualTo("POINT_포인트 결제 취소");
		assertThat(response.additionalData()).isNull();
		verify(orderRepository).findByOrderKey(mockGenericPaymentCancelRequest.paymentKey());
	}

	@Test
	@DisplayName("cancelPayment: 주문을 찾을 수 없을 때 예외 처리")
	void cancelPayment_orderNotFoundException() {
		// Given
		when(orderRepository.findByOrderKey(anyString())).thenReturn(Optional.empty());

		// When
		GenericPaymentCancelResponse response = pointPaymentGateway.cancelPayment(mockGenericPaymentCancelRequest);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.status()).isEqualTo(PaymentStatus.FAILURE);
		assertThat(response.paymentMethodDetail()).isEqualTo("UNKNOWN_ERROR");
		assertThat(response.additionalData()).containsKey("errorMessage");
		assertThat(response.additionalData().get("errorMessage")).isEqualTo("주문을 찾을 수 없습니다.");
		verify(orderRepository).findByOrderKey(mockGenericPaymentCancelRequest.paymentKey());
	}

	@Test
	@DisplayName("cancelPayment: 기타 예외 발생 시 실패 응답 반환")
	void cancelPayment_otherException() {
		String exceptionMessage = "데이터베이스 연결 오류";
		doThrow(new RuntimeException(exceptionMessage)).when(orderRepository).findByOrderKey(anyString());

		GenericPaymentCancelResponse response = pointPaymentGateway.cancelPayment(mockGenericPaymentCancelRequest);

		assertThat(response).isNotNull();
		assertThat(response.status()).isEqualTo(PaymentStatus.FAILURE);
		assertThat(response.paymentMethodDetail()).isEqualTo("UNKNOWN_ERROR");
		assertThat(response.additionalData()).containsKey("errorMessage");
		assertThat(response.additionalData().get("errorMessage")).isEqualTo(exceptionMessage);
		verify(orderRepository).findByOrderKey(mockGenericPaymentCancelRequest.paymentKey());
	}
}