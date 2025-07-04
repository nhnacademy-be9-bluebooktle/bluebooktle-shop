package shop.bluebooktle.backend.payment.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record GenericPaymentCancelResponse(
	String transactionId,

	String orderId,

	BigDecimal canceledAmount,

	PaymentStatus status,

	String paymentMethodDetail,

	Map<String, Object> additionalData
) {
}