package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.frontend.repository.RefundRepository;
import shop.bluebooktle.frontend.service.RefundService;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

	private final RefundRepository refundRepository;

	@Override
	public void requestRefund(RefundCreateRequest request) {
		refundRepository.requestRefund(request);
	}

}
