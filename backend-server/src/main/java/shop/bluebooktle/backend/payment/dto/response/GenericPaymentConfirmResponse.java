package shop.bluebooktle.backend.payment.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record GenericPaymentConfirmResponse(
	String transactionId,

	String orderId,

	BigDecimal confirmedAmount,

	PaymentStatus status,

	String paymentMethodDetail,
	
	Map<String, Object> additionalData
) {

	public enum PaymentStatus {
		SUCCESS,
		FAILURE,
		PENDING,
		CANCELLED,
		UNKNOWN
	}
}