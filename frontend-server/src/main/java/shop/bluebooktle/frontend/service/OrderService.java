package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;

public interface OrderService {
	OrderConfirmDetailResponse getOrderConfirmDetail(Long orderId);
}
