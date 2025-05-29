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
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderStateRepository orderStateRepository;
	private final UserRepository userRepository;

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

	@Override
	public OrderConfirmDetailResponse getOrderDetailsForConfirmation(Long orderId, Long userId) {
		// @EntityGraph를 사용하여 연관 데이터를 함께 로드
		Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
			.orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없거나 접근 권한이 없습니다."));

		// User 정보를 로드하여 포인트 잔액 확인
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		// TODO: 쿠폰 할인 금액 실제 계산 로직 적용 필요
		// 현재는 DTO에서 0으로 계산하고 있음. 실제 쿠폰 서비스와 연동하여 할인액을 가져오거나 계산해야 함.

		// return OrderConfirmDetailResponse.fromEntity(order, user);
		return null;
	}
}
