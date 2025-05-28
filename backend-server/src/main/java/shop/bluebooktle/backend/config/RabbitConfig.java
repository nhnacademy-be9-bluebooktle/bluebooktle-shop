package shop.bluebooktle.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	public static final String WELCOME_QUEUE = "coupon.issue.welcome.queue";
	public static final String WELCOME_EXCHANGE = "coupon.issue.welcome.exchange";
	public static final String WELCOME_ROUTING_KEY = "coupon.issue.welcome.routing-key";

	@Bean
	public Queue welcomeQueue() {
		return new Queue(WELCOME_QUEUE, true);
	}

	@Bean
	public DirectExchange welcomeExchange() {
		return new DirectExchange(WELCOME_EXCHANGE);
	}

	@Bean
	public Binding welcomeBinding() {
		return BindingBuilder
			.bind(welcomeQueue())
			.to(welcomeExchange())
			.with(WELCOME_ROUTING_KEY);
	}
}
