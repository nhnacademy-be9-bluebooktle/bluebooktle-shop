package shop.bluebooktle.backend.order.mq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.dto.response.OrderShippingMessage;
import shop.bluebooktle.backend.order.service.OrderService;

@Slf4j
@RequiredArgsConstructor
@Component
@RefreshScope
public class OrderCompleteRabbitListener {
	private final OrderService orderService;

	@RabbitListener(queues = "#{orderQueueProperties.orderComplete}")
	public void receiveOrderCompleteMessage(OrderShippingMessage message) {
		if (message == null || message.orderId() == null) {
			return;
		}
		try {
			orderService.completeOrder(message.orderId());
		} catch (Exception e) {
			log.error("Update Failed order complete. OrderId: {}, Error: {}", message.orderId(), e.getMessage(), e);
		}
	}
}