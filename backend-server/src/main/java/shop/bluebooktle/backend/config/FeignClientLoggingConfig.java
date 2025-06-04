package shop.bluebooktle.backend.config;

import org.springframework.context.annotation.Bean;

import feign.Logger;

public class FeignClientLoggingConfig {

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}
