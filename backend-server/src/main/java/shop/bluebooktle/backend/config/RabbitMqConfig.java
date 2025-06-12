package shop.bluebooktle.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.mq.properties.ExchangeProperties;
import shop.bluebooktle.backend.coupon.mq.properties.QueueProperties;
import shop.bluebooktle.backend.order.mq.properties.OrderExchangeProperties;
import shop.bluebooktle.backend.order.mq.properties.OrderQueueProperties;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {

	private final QueueProperties props;
	private final ExchangeProperties exchange;

	private final OrderQueueProperties orderProps;
	private final OrderExchangeProperties orderExchange;

	private static final int ORDER_TTL = 600000;

	private static final long SHIPPING_COMPLETE_TTL = 600000;

	//----order
	@Bean
	public DirectExchange orderExchange() {
		return new DirectExchange(orderExchange.getOrder());
	}

	@Bean
	public DirectExchange orderDlxExchange() {
		return new DirectExchange(orderExchange.getOrderDlx());
	}

	@Bean
	public Queue orderWaitQueue() {
		return QueueBuilder.durable(orderProps.getOrderWait())
			.withArgument("x-message-ttl", ORDER_TTL)
			.withArgument("x-dead-letter-exchange", orderExchange.getOrderDlx())
			.withArgument("x-dead-letter-routing-key", orderProps.getOrderCancel())
			.build();
	}

	@Bean
	public Queue orderCancelQueue() {
		return new Queue(orderProps.getOrderCancel(), true);
	}

	@Bean
	public Binding orderBinding() {
		return BindingBuilder
			.bind(orderWaitQueue())
			.to(orderExchange())
			.with(orderProps.getOrderWait());
	}

	@Bean
	public Binding orderDlxBinding() {
		return BindingBuilder
			.bind(orderCancelQueue())
			.to(orderDlxExchange())
			.with(orderProps.getOrderCancel());
	}

	@Bean
	public Queue orderShippingQueue() {
		return QueueBuilder.durable(orderProps.getOrderShipping())
			.withArgument("x-message-ttl", SHIPPING_COMPLETE_TTL)
			.withArgument("x-dead-letter-exchange", orderExchange.getOrderDlx())
			.withArgument("x-dead-letter-routing-key", orderProps.getOrderComplete())
			.build();
	}

	@Bean
	public Binding orderShippingBinding() {
		return BindingBuilder
			.bind(orderShippingQueue())
			.to(orderExchange())
			.with(orderProps.getOrderShipping());
	}

	@Bean
	public Queue orderCompleteQueue() {
		return new Queue(orderProps.getOrderComplete(), true);
	}

	@Bean
	public Binding orderCompleteDlxBinding() {
		return BindingBuilder
			.bind(orderCompleteQueue())
			.to(orderDlxExchange())
			.with(orderProps.getOrderComplete());
	}

	// ---------birthday
	@Bean
	public Queue birthdayQueue() {
		return QueueBuilder.durable(props.getBirthday())
			.withArgument("x-dead-letter-exchange", exchange.getBirthdayDlx())
			.withArgument("x-dead-letter-routing-key", props.getBirthdayDlq())
			.build();
	}

	@Bean
	public DirectExchange birthdayExchange() {
		return new DirectExchange(exchange.getBirthday());
	}

	@Bean
	public Binding birthdayBinding() {
		return BindingBuilder
			.bind(birthdayQueue())
			.to(birthdayExchange())
			.with(props.getBirthday());
	}

	@Bean
	public Queue birthdayDlqQueue() {
		return new Queue(props.getBirthdayDlq(), true);
	}

	@Bean
	public DirectExchange birthdayDlxExchange() {
		return new DirectExchange(exchange.getBirthdayDlx());
	}

	@Bean
	public Binding birthdayDlqBinding() {
		return BindingBuilder.bind(birthdayDlqQueue())
			.to(birthdayDlxExchange())
			.with(props.getBirthdayDlq());
	}

	// ------- Direct -------
	@Bean
	public Queue directQueue() {
		return QueueBuilder.durable(props.getDirect())
			.withArgument("x-dead-letter-exchange", exchange.getDirectDlx())
			.withArgument("x-dead-letter-routing-key", props.getDirectDlq())
			.build();
	}

	@Bean
	public DirectExchange directExchange() {
		return new DirectExchange(exchange.getDirect());
	}

	@Bean
	public Binding directBinding() {
		return BindingBuilder.bind(directQueue())
			.to(directExchange())
			.with(props.getDirect());
	}

	@Bean
	public Queue directDlqQueue() {
		return new Queue(props.getDirectDlq(), true);
	}

	@Bean
	public DirectExchange directDlxExchange() {
		return new DirectExchange(exchange.getDirectDlx());
	}

	@Bean
	public Binding directDlqBinding() {
		return BindingBuilder.bind(directDlqQueue())
			.to(directDlxExchange())
			.with(props.getDirectDlq());
	}

	// ------- welcome -------
	@Bean
	public Queue welcomeQueue() {
		return QueueBuilder.durable(props.getWelcome())
			.withArgument("x-dead-letter-exchange", exchange.getWelcomeDlx())
			.withArgument("x-dead-letter-routing-key", props.getWelcomeDlq())
			.build();
	}

	@Bean
	public DirectExchange welcomeExchange() {
		return new DirectExchange(exchange.getWelcome());
	}

	@Bean
	public Binding welcomeBinding() {
		return BindingBuilder.bind(welcomeQueue())
			.to(welcomeExchange())
			.with(props.getWelcome());
	}

	@Bean
	public Queue welcomeDlqQueue() {
		return new Queue(props.getWelcomeDlq(), true);
	}

	@Bean
	public DirectExchange welcomeDlxExchange() {
		return new DirectExchange(exchange.getWelcomeDlx());
	}

	@Bean
	public Binding welcomeDlqBinding() {
		return BindingBuilder.bind(welcomeDlqQueue())
			.to(welcomeDlxExchange())
			.with(props.getWelcomeDlq());
	}

	//예외 발생 시 Reject
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
		MessageConverter messageConverter) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(messageConverter);
		factory.setDefaultRequeueRejected(false);
		return factory;
	}

	// JSON 컨버터
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}
}
