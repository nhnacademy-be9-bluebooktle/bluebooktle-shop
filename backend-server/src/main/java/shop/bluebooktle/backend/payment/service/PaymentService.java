package shop.bluebooktle.backend.payment.service;

import shop.bluebooktle.backend.payment.entity.Payment;

public interface PaymentService {
	
	Long create(Payment payment);

	Payment get(Long id);

	void delete(Long id);
}
