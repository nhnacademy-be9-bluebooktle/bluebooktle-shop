package shop.bluebooktle.auth.config;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

class PaycoFeignConfigTest {

	PaycoFeignConfig config = new PaycoFeignConfig();

	@Test
	@DisplayName("feignLoggerLevel()은 Logger.Level.FULL을 반환한다")
	void feignLoggerLevel_returnsFull() {
		assertThat(config.feignLoggerLevel()).isEqualTo(feign.Logger.Level.FULL);
	}

	@Test
	@DisplayName("feignErrorDecoder()는 응답 상태코드에 따라 ApplicationException을 던진다")
	void feignErrorDecoder_returnsApplicationException() {
		// given
		ErrorDecoder decoder = config.feignErrorDecoder();

		Response response = Response.builder()
			.status(401)
			.reason("Unauthorized")
			.request(Request.create(Request.HttpMethod.GET, "/payco/profile", Collections.emptyMap(), null,
				new RequestTemplate()))
			.headers(Collections.emptyMap())
			.body("Unauthorized", StandardCharsets.UTF_8)
			.build();

		// when
		Throwable thrown = decoder.decode("PaycoClient#getProfile", response);

		// then
		assertThat(thrown).isInstanceOf(ApplicationException.class);
		ApplicationException ex = (ApplicationException)thrown;
		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AUTH_OAUTH_LOGIN_FAILED);
		assertThat(ex.getMessage()).contains("PAYCO API 호출 실패");
	}
}
