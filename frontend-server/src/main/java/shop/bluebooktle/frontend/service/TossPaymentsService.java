package shop.bluebooktle.frontend.service;

import org.springframework.stereotype.Service;

import shop.bluebooktle.common.dto.payment.reponse.TossConfirmResponse;
import shop.bluebooktle.common.dto.payment.request.TossConfirmRequest;
import shop.bluebooktle.frontend.repository.TossPaymentsRepository;

@Service
public class TossPaymentsService {
	private final TossPaymentsRepository client;

	public TossPaymentsService(TossPaymentsRepository client) {
		this.client = client;
	}

	public TossConfirmResponse confirm(TossConfirmRequest req) {
		return client.confirmPayment(req);
	}
}