package shop.bluebooktle.backend.order.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.domain.OrderStatus;
import shop.bluebooktle.common.entity.auth.User;

public interface OrderService {

	Page<Order> getUserOrders(User user, OrderStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable);

	Order getOrderByOrderKey(UUID orderKey);

	void updateOrderStatus(Long orderId, OrderStatus newStatus);
}
