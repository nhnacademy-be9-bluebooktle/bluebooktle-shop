package shop.bluebooktle.frontend.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.common.dto.refund.response.RefundListResponse;
import shop.bluebooktle.frontend.repository.AdminRefundRepository;
import shop.bluebooktle.frontend.service.AdminRefundService;

@Service
@RequiredArgsConstructor
public class AdminRefundServiceImpl implements AdminRefundService {

	private final AdminRefundRepository adminRefundRepository;

	@Override
	public void updateRefund(RefundUpdateRequest request) {
		adminRefundRepository.updateRefund(request);
	}

	@Override
	public PaginationData<RefundListResponse> getRefunds(RefundSearchRequest request, Pageable pageable) {
		return adminRefundRepository.getRefunds(request, pageable);
	}

	@Override
	public AdminRefundDetailResponse getRefundDetail(Long refundId) {
		return adminRefundRepository.getRefundDetail(refundId);
	}
}