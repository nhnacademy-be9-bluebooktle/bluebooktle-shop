package shop.bluebooktle.backend.payment.gateway;

import shop.bluebooktle.backend.payment.dto.request.GenericPaymentCancelRequest;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentCancelResponse;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentConfirmResponse;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;

public interface PaymentGateway {

	String getGatewayName();

	GenericPaymentConfirmResponse confirmPayment(PaymentConfirmRequest commonRequest);

	GenericPaymentCancelResponse cancelPayment(GenericPaymentCancelRequest commonRequest);
}