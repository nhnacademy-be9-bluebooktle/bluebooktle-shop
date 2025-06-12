package shop.bluebooktle.backend.payment.dto.response;

public record TossApiPaymentConfirmSuccessResponse(
	String paymentKey,
	String orderId,
	String status,
	Long totalAmount,
	String method
) {
}