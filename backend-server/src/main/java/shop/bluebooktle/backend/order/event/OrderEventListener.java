package shop.bluebooktle.backend.order.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.dto.response.OrderCancelMessage;
import shop.bluebooktle.backend.order.dto.response.OrderShippingMessage;
import shop.bluebooktle.backend.order.mq.properties.OrderExchangeProperties;
import shop.bluebooktle.backend.order.mq.properties.OrderQueueProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

	private final RabbitTemplate rabbitTemplate;
	private final OrderExchangeProperties orderExchange;
	private final OrderQueueProperties orderQueue;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleOrderCancelIssue(OrderCancelMessage event) {
		try {
			rabbitTemplate.convertAndSend(
				orderExchange.getOrder(),
				orderQueue.getOrderWait(),
				event
			);
		} catch (Exception e) {
			log.error("order mq test failed orderID: {},{}", event.orderId(), e.getMessage());
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleOrderShipping(OrderShippingMessage event) {
		try {

			rabbitTemplate.convertAndSend(
				orderExchange.getOrder(),
				orderQueue.getOrderShipping(),
				event
			);
		} catch (Exception e) {
			log.error("Order shipping MQ message failed. OrderId: {}, Error: {}", event.orderId(), e.getMessage());
		}
	}
}