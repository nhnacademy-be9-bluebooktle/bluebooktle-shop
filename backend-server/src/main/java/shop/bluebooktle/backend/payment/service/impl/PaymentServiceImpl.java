package shop.bluebooktle.backend.payment.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.payment.service.PaymentService;
import shop.bluebooktle.common.exception.payment.PaymentNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;

	@Override
	@Transactional
	public Long create(Payment payment) {
		return paymentRepository.save(payment).getId();
	}

	@Override
	public Payment get(Long id) {
		return paymentRepository.findById(id)
			.orElseThrow(PaymentNotFoundException::new);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Payment payment = paymentRepository.findById(id)
			.orElseThrow(PaymentNotFoundException::new);
		paymentRepository.delete(payment);
	}
}
