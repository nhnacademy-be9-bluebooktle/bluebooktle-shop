package shop.bluebooktle.backend.order.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderStateRepository orderStateRepository;

	@Override
	public List<Order> getUserOrders(User user, OrderStatus status, LocalDateTime start, LocalDateTime end,
		Pageable pageable) {
		if (status == null && start != null && end != null) {
			return orderRepository.findByUserAndCreatedAtBetween(user, start, end, pageable);
		} else if (status != null && start == null && end == null) {
			return orderRepository.findByUserAndOrderState_State(user, status, pageable);
		} else if (status != null && start != null && end != null) {
			return orderRepository.findByUserAndOrderState_StateAndCreatedAtBetween(user, status, start, end, pageable);
		} else {
			return orderRepository.findByUser(user, pageable); // 기본 전체 조회
		}
	}

	@Override
	public Order getOrderByOrderKey(String orderKey) {
		return orderRepository.findByOrderKey(orderKey)
			.orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));
	}

}
