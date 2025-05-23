package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.frontend.repository.TossPaymentsRepository;
import shop.bluebooktle.frontend.service.PaymentsService;

@Service
public class TossPaymentsServiceImpl implements PaymentsService {
	private final TossPaymentsRepository client;

	public TossPaymentsServiceImpl(TossPaymentsRepository client) {
		this.client = client;
	}

	public PaymentConfirmResponse confirm(PaymentConfirmRequest req) {
		return client.confirmPayment(req);
	}
}