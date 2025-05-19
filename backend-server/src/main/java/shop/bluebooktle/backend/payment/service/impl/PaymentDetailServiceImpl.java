// 4. Service 구현
package shop.bluebooktle.backend.payment.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.dto.request.PaymentDetailRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentDetailResponse;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.payment.repository.PaymentDetailRepository;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.payment.repository.PaymentTypeRepository;
import shop.bluebooktle.backend.payment.service.PaymentDetailService;
import shop.bluebooktle.common.exception.payment.PaymentDetailNotFoundException;
import shop.bluebooktle.common.exception.payment.PaymentNotFoundException;
import shop.bluebooktle.common.exception.payment.PaymentTypeNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentDetailServiceImpl implements PaymentDetailService {

	private final PaymentRepository paymentRepository;
	private final PaymentDetailRepository detailRepo;
	private final PaymentTypeRepository typeRepo;

	@Override
	@Transactional
	public void create(PaymentDetailRequest req) {
		PaymentType type = typeRepo.findById(req.paymentTypeId())
			.orElseThrow(() -> new PaymentTypeNotFoundException());
		
		Payment payment = paymentRepository.findById(req.paymentId())
			.orElseThrow(() -> new PaymentNotFoundException());
		detailRepo.save(req.toEntity(payment, type));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		PaymentDetail detail = detailRepo.findById(id)
			.orElseThrow(() -> new PaymentDetailNotFoundException());
		detailRepo.delete(detail);
	}

	@Override
	public PaymentDetailResponse get(Long id) {
		PaymentDetail detail = detailRepo.findById(id)
			.orElseThrow(() -> new PaymentDetailNotFoundException());
		return PaymentDetailResponse.fromEntity(detail);
	}
}
