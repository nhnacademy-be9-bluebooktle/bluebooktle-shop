package shop.bluebooktle.frontend.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class TossFeignConfig {

	@Value("${toss.secret-key}")
	private String secretKey;

	@Bean
	public RequestInterceptor tossAuthInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate template) {
				String token = Base64.getEncoder()
					.encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
				template.header("Authorization", "Basic " + token);
				template.header("Content-Type", "application/json");
			}
		};
	}
}
