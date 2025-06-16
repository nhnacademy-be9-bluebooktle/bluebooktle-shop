package shop.bluebooktle.backend.coupon.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.backend.user.dto.DoorayMessagePayload;

@FeignClient(name = "CouponFailureMessageClient", url = "${dooray.webhook.coupon-failure-channel}")
public interface CouponFailureMessageClient {
	@PostMapping
	String sendMessage(@RequestBody DoorayMessagePayload payload);
}