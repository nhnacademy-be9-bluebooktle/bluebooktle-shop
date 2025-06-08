package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.frontend.repository.OrderRepository;
import shop.bluebooktle.frontend.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;

	public OrderConfirmDetailResponse getOrderConfirmDetail(Long orderId) {
		return orderRepository.getOrderConfirmDetail(orderId);
	}

	@Override
	public Long createOrder(OrderCreateRequest orderCreateRequest) {
		return orderRepository.createOrder(orderCreateRequest);
	}

	@Override
	public PaginationData<OrderHistoryResponse> getOrderHistory(
		int page,
		int size,
		OrderStatus status
	) {
		return orderRepository.getOrderHistory(page, size, status);
	}

	@Override
	public void cancelOrder(String orderKey) {
		orderRepository.cancelOrder(orderKey);
	}

	@Override
	public OrderDetailResponse getOrderDetailByOrderKey(String orderKey) {
		return orderRepository.getOrderDetailByOrderKey(orderKey);
	}

}
