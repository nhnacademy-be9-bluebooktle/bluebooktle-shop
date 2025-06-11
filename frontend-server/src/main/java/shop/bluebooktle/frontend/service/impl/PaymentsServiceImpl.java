package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.payment.request.PaymentCancelRequest;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.frontend.repository.PaymentRepository;
import shop.bluebooktle.frontend.service.PaymentsService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsServiceImpl implements PaymentsService {

	private final PaymentRepository paymentRepository;

	@Override
	public PaymentConfirmResponse confirm(PaymentConfirmRequest req, String paymentMethod) {
		return paymentRepository.confirmPayment(paymentMethod.toUpperCase(), req);
	}

	@Override
	public Void cancel(PaymentCancelRequest req) {
		// 현재는 TOSS로 고정
		String gatewayName = "TOSS";
		return paymentRepository.cancelPayment(gatewayName, req);
	}
}