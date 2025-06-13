package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(
	url = "${server.gateway-url}",
	name = "refundRepository",
	path = "/api/refunds",
	configuration = FeignGlobalConfig.class
)
public interface RefundRepository {

	@PostMapping("/request")
	@RetryWithTokenRefresh
	Void requestRefund(@RequestBody RefundCreateRequest request);
}
