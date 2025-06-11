package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.common.dto.refund.response.RefundListResponse;

public interface AdminRefundService {

	void updateRefund(RefundUpdateRequest request);

	PaginationData<RefundListResponse> getRefunds(RefundSearchRequest request, Pageable pageable);

	AdminRefundDetailResponse getRefundDetail(Long refundId);
}
