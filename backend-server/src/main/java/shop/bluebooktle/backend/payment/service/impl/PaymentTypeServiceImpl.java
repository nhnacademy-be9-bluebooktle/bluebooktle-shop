package shop.bluebooktle.backend.payment.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentTypeResponse;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.payment.repository.PaymentTypeRepository;
import shop.bluebooktle.backend.payment.service.PaymentTypeService;
import shop.bluebooktle.common.exception.payment.PaymentTypeAlreadyExistException;
import shop.bluebooktle.common.exception.payment.PaymentTypeNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentTypeServiceImpl implements PaymentTypeService {

	private final PaymentTypeRepository paymentTypeRepository;

	@Override
	@Transactional
	public PaymentTypeResponse create(PaymentTypeRequest paymentTypeRequest) {
		if (paymentTypeRepository.existsByMethod(paymentTypeRequest.method())) {
			throw new PaymentTypeAlreadyExistException("이미 존재하는 결제수단: ");
		}

		PaymentType pt = paymentTypeRepository.save(paymentTypeRequest.toEntity());
		return PaymentTypeResponse.fromEntity(pt);
	}

	@Override
	@Transactional
	public void update(PaymentTypeRequest oldPaymentType, PaymentTypeRequest newPaymentType) {
		PaymentType pt = paymentTypeRepository.findByMethod(oldPaymentType.method())
			.orElseThrow(() -> new PaymentTypeNotFoundException("결제수단 없음"));

		if (!pt.getMethod().equals(newPaymentType.method())
			&& paymentTypeRepository.existsByMethod(newPaymentType.method())) {
			throw new PaymentTypeAlreadyExistException("이미 존재하는 결제수단");
		}
		pt.changeMethod(newPaymentType.method());
		paymentTypeRepository.save(pt);
	}

	@Override
	@Transactional
	public void delete(PaymentTypeRequest paymentTypeRequest) {
		PaymentType pt = paymentTypeRepository.findByMethod(paymentTypeRequest.method())
			.orElseThrow(() -> new PaymentTypeNotFoundException("결제수단 없음"));
		paymentTypeRepository.delete(pt);
	}

	@Override
	public PaymentTypeResponse get(PaymentTypeRequest paymentTypeRequest) {
		PaymentType pt = paymentTypeRepository.findByMethod(paymentTypeRequest.method())
			.orElseThrow(() -> new PaymentTypeNotFoundException("결제수단 없음"));
		return PaymentTypeResponse.fromEntity(pt);
	}

	@Override
	public List<PaymentTypeResponse> getAll() {
		return paymentTypeRepository.findAll()
			.stream()
			.map(PaymentTypeResponse::fromEntity)
			.toList();
	}
}
