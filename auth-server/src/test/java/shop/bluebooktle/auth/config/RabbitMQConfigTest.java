package shop.bluebooktle.auth.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

class RabbitMQConfigTest {

	RabbitMQConfig config = new RabbitMQConfig();

	@Test
	@DisplayName("jsonMessageConverter()는 Jackson2JsonMessageConverter를 반환한다")
	void jsonMessageConverter_returnsJacksonConverter() {
		MessageConverter converter = config.jsonMessageConverter();
		assertThat(converter).isInstanceOf(Jackson2JsonMessageConverter.class);
	}

	@Test
	@DisplayName("rabbitTemplate()은 주입된 의존성으로 RabbitTemplate을 생성한다")
	void rabbitTemplate_createsRabbitTemplate() {
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		MessageConverter mockMessageConverter = mock(MessageConverter.class);

		RabbitTemplate rabbitTemplate = config.rabbitTemplate(mockConnectionFactory, mockMessageConverter);

		assertThat(rabbitTemplate).isNotNull();
		assertThat(rabbitTemplate.getConnectionFactory()).isEqualTo(mockConnectionFactory);
		assertThat(rabbitTemplate.getMessageConverter()).isEqualTo(mockMessageConverter);
	}
}
