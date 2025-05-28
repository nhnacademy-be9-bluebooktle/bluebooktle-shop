package shop.bluebooktle.backend.order.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.entity.auth.User;

public interface OrderService {

	List<Order> getUserOrders(User user, OrderStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable);

	Order getOrderByOrderKey(String orderKey);

}
