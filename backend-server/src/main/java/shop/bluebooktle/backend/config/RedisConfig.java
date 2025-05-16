package shop.bluebooktle.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import shop.bluebooktle.backend.cart.dto.CartItemRequest;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, CartItemRequest> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, CartItemRequest> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);

		template.setKeySerializer(new StringRedisSerializer());

		// 여기서도 setObjectMapper는 deprecated 상태지만,
		// Jackson2JsonRedisSerializer(CartItemRequestDto.class) 생성자로 타입 고정
		Jackson2JsonRedisSerializer<CartItemRequest> serializer =
			new Jackson2JsonRedisSerializer<>(CartItemRequest.class);
		template.setValueSerializer(serializer);
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(serializer);

		return template;
	}
}