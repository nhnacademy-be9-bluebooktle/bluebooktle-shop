package shop.bluebooktle.frontend.config.feign;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.frontend.config.feign.decoder.GlobalFeignErrorDecoder;
import shop.bluebooktle.frontend.config.feign.decoder.JsendDecoder;
import shop.bluebooktle.frontend.config.feign.interceptor.FeignBearerTokenInterceptor;
import shop.bluebooktle.frontend.service.AuthService;
import shop.bluebooktle.frontend.util.CookieTokenUtil;

@RequiredArgsConstructor
public class FeignGlobalConfig {

	private final ObjectMapper feignObjectMapperInstance;
	private final CookieTokenUtil cookieTokenUtil;
	private final ObjectProvider<AuthService> authServiceProvider;

	@Bean
	public FeignBearerTokenInterceptor feignBearerTokenInterceptor() {
		return new FeignBearerTokenInterceptor(this.cookieTokenUtil);
	}

	@Bean
	public Decoder feignDecoder() {
		return new JsendDecoder(this.feignObjectMapperInstance);
	}

	@Bean
	public ErrorDecoder globalFeignErrorDecoder() {
		return new GlobalFeignErrorDecoder(
			this.feignObjectMapperInstance,
			this.cookieTokenUtil,
			this.authServiceProvider);
	}

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	public Retryer feignRetryer() {
		return Retryer.NEVER_RETRY;
	}
}