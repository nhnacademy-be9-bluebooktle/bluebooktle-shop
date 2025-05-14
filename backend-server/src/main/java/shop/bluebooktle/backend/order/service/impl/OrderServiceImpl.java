package shop.bluebooktle.backend.order.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.OrderStatus;
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
			return orderRepository.findByUserAndOrderDateBetween(user, start, end, pageable);
		} else if (status != null && start == null && end == null) {
			return orderRepository.findByUserAndOrderState_State(user, status, pageable);
		} else if (status != null && start != null && end != null) {
			return orderRepository.findByUserAndOrderState_StateAndOrderDateBetween(user, status, start, end, pageable);
		} else {
			return orderRepository.findByUser(user, pageable); // 기본 전체 조회
		}
	}

	@Override
	public Order getOrderByOrderKey(UUID orderKey) {
		return orderRepository.findByOrderKey(orderKey)
			.orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));
	}

	@Override
	@Transactional
	public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
		OrderState state = orderStateRepository.findByState(newStatus)
			.orElseThrow(() -> new IllegalArgumentException("해당 상태가 존재하지 않습니다."));
		order.changeOrderState(state);
	}
}
