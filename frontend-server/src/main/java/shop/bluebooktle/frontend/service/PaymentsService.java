package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.payment.request.PaymentCancelRequest;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;

public interface PaymentsService {

	PaymentConfirmResponse confirm(PaymentConfirmRequest req, String paymentMethod);

	Void cancel(PaymentCancelRequest req);
}