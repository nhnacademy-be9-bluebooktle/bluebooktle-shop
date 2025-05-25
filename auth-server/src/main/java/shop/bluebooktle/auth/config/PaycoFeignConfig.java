package shop.bluebooktle.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

@Slf4j
@Configuration
public class PaycoFeignConfig {

	// Feign 에러 디코더 설정
	@Bean
	public ErrorDecoder feignErrorDecoder() {
		return (methodKey, response) -> {
			log.error("PAYCO Feign Client Error: Status={}, Reason={}, Method={}",
				response.status(), response.reason(), methodKey);

			return new ApplicationException(ErrorCode.AUTH_OAUTH_LOGIN_FAILED,
				"PAYCO API 호출 실패: Status=" + response.status() + " (" + methodKey + ")");
		};
	}

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

}