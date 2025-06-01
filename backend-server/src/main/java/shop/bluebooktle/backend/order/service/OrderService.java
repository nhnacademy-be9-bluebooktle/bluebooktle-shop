package shop.bluebooktle.backend.order.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.common.entity.auth.User;

public interface OrderService {

	Page<OrderHistoryResponse> getUserOrders(User user, OrderStatus status, LocalDateTime start, LocalDateTime end,
		Pageable pageable);

	Order getOrderByOrderKey(String orderKey);

	OrderConfirmDetailResponse getOrderDetailsForConfirmation(Long orderId, Long userId);

	Long createOrder(OrderCreateRequest request);

}
