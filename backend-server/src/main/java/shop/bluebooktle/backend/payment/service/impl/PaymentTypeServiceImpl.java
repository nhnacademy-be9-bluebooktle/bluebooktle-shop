package shop.bluebooktle.backend.payment.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public void create(PaymentTypeRequest paymentTypeRequest) {
		if (paymentTypeRepository.existsByMethod(paymentTypeRequest.method())) {
			throw new PaymentTypeAlreadyExistException();
		}

		paymentTypeRepository.save(paymentTypeRequest.toEntity());
	}

	@Override
	@Transactional
	public void update(Long id, PaymentTypeRequest newPaymentTypeRequest) {
		PaymentType pt = paymentTypeRepository.findById(id)
			.orElseThrow(() -> new PaymentTypeNotFoundException());

		if (!pt.getMethod().equals(newPaymentTypeRequest.method())
			&& paymentTypeRepository.existsByMethod(newPaymentTypeRequest.method())) {
			throw new PaymentTypeAlreadyExistException();
		}
		pt.changeMethod(newPaymentTypeRequest.method());
		paymentTypeRepository.save(pt);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		PaymentType pt = paymentTypeRepository.findById(id)
			.orElseThrow(() -> new PaymentTypeNotFoundException());
		paymentTypeRepository.delete(pt);
	}

	@Override
	public PaymentTypeResponse get(PaymentTypeRequest paymentTypeRequest) {
		PaymentType pt = paymentTypeRepository.findByMethod(paymentTypeRequest.method())
			.orElseThrow(() -> new PaymentTypeNotFoundException());
		return PaymentTypeResponse.fromEntity(pt);
	}

	@Override
	public Page<PaymentTypeResponse> getAll(Pageable pageable) {
		return paymentTypeRepository.findAll(pageable).map(PaymentTypeResponse::fromEntity);
	}
}
