package shop.bluebooktle.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import feign.Logger;
import feign.codec.ErrorDecoder;

@Configuration
public class FeignGlobalConfig {

	@Bean
	public ObjectMapper feignObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

	@Bean
	public ErrorDecoder globalFeignErrorDecoder(ObjectMapper objectMapper) {
		return new GlobalFeignErrorDecoder(objectMapper);
	}

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}