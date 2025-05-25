package shop.bluebooktle.frontend.config;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@EnableCaching
@Configuration
public class RedisCacheConfig {
	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
		// 1) JSON Serializer 준비
		GenericJackson2JsonRedisSerializer jsonSerializer =
			new GenericJackson2JsonRedisSerializer();

		// 2) SerializationPair 생성
		RedisSerializationContext.SerializationPair<Object> pair =
			RedisSerializationContext.SerializationPair
				.fromSerializer(jsonSerializer);

		// 3) 캐시 구성에 적용
		RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new GenericJackson2JsonRedisSerializer()
				)
			)
			.entryTtl(Duration.ofHours(1))
			.disableCachingNullValues();

		return RedisCacheManager.builder(factory)
			.cacheDefaults(conf)
			.transactionAware()
			.build();
	}
}
