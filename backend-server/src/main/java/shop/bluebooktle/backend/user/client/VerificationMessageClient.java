package shop.bluebooktle.backend.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.backend.user.dto.DoorayMessagePayload;

@FeignClient(name = "VerificationMessageClient", url = "${dooray.webhook.verification-channel}")
public interface VerificationMessageClient {
	@PostMapping
	String sendMessage(@RequestBody DoorayMessagePayload doorayMessagePayload);
}
