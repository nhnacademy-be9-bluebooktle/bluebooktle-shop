package shop.bluebooktle.backend.order.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.order.OrderStatus;

@Service
public class OrderCompletionScheduler {

	private final OrderRepository orderRepository;
	private final OrderService orderService;

	public OrderCompletionScheduler(OrderRepository orderRepository, OrderService orderService) {
		this.orderRepository = orderRepository;
		this.orderService = orderService;
	}

	@Scheduled(cron = "0 0 0/6 * * *")
	@Transactional
	public void completeOverdueOrders() {

		List<Order> overdueOrders = orderRepository.findOrdersByStatusAndRequestedDeliveryDateBefore(
			OrderStatus.SHIPPING,
			LocalDateTime.now()
		);
		if (overdueOrders.isEmpty()) {
			return;
		}
		for (Order order : overdueOrders) {
			orderService.completeOrder(order.getId());
		}
	}
}