package shop.bluebooktle.backend.order.mq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.dto.response.OrderCancelMessage;
import shop.bluebooktle.backend.order.service.OrderService;

@Slf4j
@RequiredArgsConstructor
@Component
@RefreshScope
public class OrderCancelRabbitListener {
	private final OrderService orderService;

	@RabbitListener(queues = "#{orderQueueProperties.orderCancel}")
	public void receiveOrderCancelMessage(OrderCancelMessage message) {
		if (message == null || message.orderId() == null) {
			return;
		}
		try {
			orderService.cancelOrderListener(message.orderId());
		} catch (Exception e) {
			log.error("order cancel failed. orderId: {}, {}", message.orderId(), e.getMessage(), e);
		}
	}
}
