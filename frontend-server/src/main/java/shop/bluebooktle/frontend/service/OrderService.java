package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;

public interface OrderService {
	OrderConfirmDetailResponse getOrderConfirmDetail(Long orderId);

	Long createOrder(OrderCreateRequest orderCreateRequest);
}
