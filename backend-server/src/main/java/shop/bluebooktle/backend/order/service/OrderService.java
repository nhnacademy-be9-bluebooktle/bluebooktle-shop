package shop.bluebooktle.backend.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;

public interface OrderService {

	Page<OrderHistoryResponse> getUserOrders(Long userId, OrderStatus status, Pageable pageable);

	Order getOrderByOrderKey(String orderKey);

	OrderConfirmDetailResponse getOrderDetailsForConfirmation(Long orderId, Long userId);

	Long createOrder(OrderCreateRequest request);

}
