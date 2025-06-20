package shop.bluebooktle.backend.payment.service;

import shop.bluebooktle.common.dto.payment.request.PaymentCancelRequest;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;

public interface PaymentService {
	void confirmPayment(PaymentConfirmRequest request, String gatewayName);

	void cancelPayment(PaymentCancelRequest request, Long userId);
}