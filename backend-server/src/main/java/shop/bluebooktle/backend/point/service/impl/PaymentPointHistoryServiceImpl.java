package shop.bluebooktle.backend.point.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.point.entity.PaymentPointHistory;
import shop.bluebooktle.backend.point.repository.PaymentPointHistoryRepository;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.backend.point.service.PaymentPointHistoryService;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.exception.payment.PaymentNotFoundException;
import shop.bluebooktle.common.exception.point.PointHistoryNotFoundException;

@Service
@RequiredArgsConstructor
public class PaymentPointHistoryServiceImpl implements PaymentPointHistoryService {
	private final PaymentPointHistoryRepository paymentPointHistoryRepository;
	private final PaymentRepository paymentRepository;
	private final PointHistoryRepository pointHistoryRepository;

	@Override
	public void save(Long paymentId, Long pointHistoryId) {
		Payment payment = paymentRepository.findById(paymentId).orElseThrow(PaymentNotFoundException::new);
		PointHistory pointHistory = pointHistoryRepository.findById(pointHistoryId)
			.orElseThrow(PointHistoryNotFoundException::new);
		paymentPointHistoryRepository.save(new PaymentPointHistory(payment, pointHistory));
	}
}