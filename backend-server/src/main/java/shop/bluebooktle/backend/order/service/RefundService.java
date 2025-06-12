package shop.bluebooktle.backend.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.common.dto.refund.response.RefundListResponse;

public interface RefundService {

	void requestRefund(Long userId, RefundCreateRequest request);

	void updateRefund(RefundUpdateRequest request);

	Page<RefundListResponse> getRefundList(RefundSearchRequest request, Pageable pageable);

	AdminRefundDetailResponse getAdminRefundDetail(Long refundId);
}
