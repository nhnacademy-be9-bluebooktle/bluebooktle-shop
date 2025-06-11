package shop.bluebooktle.backend.order.service;

import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;

public interface RefundService {

	void requestRefund(Long userId, RefundCreateRequest request);

	void updateRefund(RefundUpdateRequest request);
}
