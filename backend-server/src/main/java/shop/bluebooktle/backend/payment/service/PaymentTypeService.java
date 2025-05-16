package shop.bluebooktle.backend.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentTypeResponse;

public interface PaymentTypeService {

	void create(PaymentTypeRequest paymentTypeRequest);

	void update(Long id, PaymentTypeRequest newPaymentTypeRequest);

	void delete(Long id);

	PaymentTypeResponse get(PaymentTypeRequest paymentTypeRequest);

	Page<PaymentTypeResponse> getAll(Pageable pageable);
}
