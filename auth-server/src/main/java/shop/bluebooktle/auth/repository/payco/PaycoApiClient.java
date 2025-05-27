package shop.bluebooktle.auth.repository.payco;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import shop.bluebooktle.auth.config.PaycoFeignConfig;
import shop.bluebooktle.common.dto.auth.request.PaycoProfileResponse;

@FeignClient(name = "payco-api", url = "${oauth.payco.api-url}", configuration = PaycoFeignConfig.class)
public interface PaycoApiClient {

	@PostMapping("/payco/friends/find_member_v2.json")
	PaycoProfileResponse getProfile(
		@RequestHeader("access_token") String accessToken,
		@RequestHeader("client_id") String clientId
	);
}