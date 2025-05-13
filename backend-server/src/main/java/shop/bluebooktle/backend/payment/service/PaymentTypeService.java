package shop.bluebooktle.backend.payment.service;

import java.util.List;

import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentTypeResponse;

public interface PaymentTypeService {

	PaymentTypeResponse create(PaymentTypeRequest paymentTypeRequest);

	void update(PaymentTypeRequest oldPaymentType, PaymentTypeRequest newPaymentType);

	void delete(PaymentTypeRequest paymentTypeRequest);

	PaymentTypeResponse get(PaymentTypeRequest paymentTypeRequest);

	List<PaymentTypeResponse> getAll();
}
