package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.frontend.repository.OrderRepository;
import shop.bluebooktle.frontend.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;

	public OrderConfirmDetailResponse getOrderConfirmDetail(Long orderId) {
		return orderRepository.getOrderConfirmDetail(orderId);
	}
}
