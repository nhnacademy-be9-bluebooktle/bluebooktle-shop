package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;

public interface RefundService {
	void requestRefund(RefundCreateRequest request);

}
