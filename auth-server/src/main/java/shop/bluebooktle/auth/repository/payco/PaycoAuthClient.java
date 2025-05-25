package shop.bluebooktle.auth.repository.payco;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import shop.bluebooktle.auth.config.PaycoFeignConfig;
import shop.bluebooktle.common.dto.auth.request.PaycoTokenResponse;

@FeignClient(name = "payco-auth", url = "${oauth.payco.auth-url}", configuration = PaycoFeignConfig.class)
public interface PaycoAuthClient {

	@PostMapping(value = "/oauth2.0/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	PaycoTokenResponse getToken(Map<String, ?> formParams);
}