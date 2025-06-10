package shop.bluebooktle.backend.payment.dto.request;

public record GenericPaymentCancelRequest(
	String paymentKey,

	String cancelReason
) {
}