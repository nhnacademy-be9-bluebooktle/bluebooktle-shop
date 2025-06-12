package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;

public interface OrderService {
	OrderConfirmDetailResponse getOrderConfirmDetail(Long orderId);

	Long createOrder(OrderCreateRequest orderCreateRequest, String guestId);

	PaginationData<OrderHistoryResponse> getOrderHistory(int page, int size, OrderStatus status);

	void cancelOrder(String orderKey);

	OrderDetailResponse getOrderDetailByOrderKey(String orderKey);

	OrderConfirmDetailResponse getOrderByKey(String orderKey);
}
