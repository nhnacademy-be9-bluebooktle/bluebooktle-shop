package shop.bluebooktle.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Logger;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class FeignGlobalConfig {

	private final ObjectMapper feignObjectMapperInstance;

	@Bean
	public FeignBearerTokenInterceptor feignBearerTokenInterceptor() {
		return new FeignBearerTokenInterceptor();
	}

	@Bean
	public Decoder feignDecoder() {
		return new JsendDecoder(this.feignObjectMapperInstance);
	}

	@Bean
	public ErrorDecoder globalFeignErrorDecoder() {
		return new GlobalFeignErrorDecoder(this.feignObjectMapperInstance);
	}

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}