package shop.bluebooktle.backend.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;

public interface OrderService {

	Page<OrderHistoryResponse> getUserOrders(Long userId, OrderStatus status, Pageable pageable);

	Long createOrder(OrderCreateRequest request);

	OrderConfirmDetailResponse getOrderByKey(String orderKey, Long userId);

	OrderConfirmDetailResponse getOrderById(Long orderId, Long userId);

	void shipOrder(Long orderId);
}
