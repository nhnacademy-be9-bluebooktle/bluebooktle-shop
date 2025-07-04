package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.payment.request.PaymentCancelRequest;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", name = "PaymentRepository", path = "/api/payments", configuration = FeignGlobalConfig.class)
public interface PaymentRepository {
	@PostMapping("/{gatewayName}/confirm")
	@RetryWithTokenRefresh
	PaymentConfirmResponse confirmPayment(
		@PathVariable("gatewayName") String gatewayName,
		@RequestBody PaymentConfirmRequest request
	);

	@PostMapping("/cancel")
	@RetryWithTokenRefresh
	Void cancelPayment(
		@RequestBody PaymentCancelRequest request
	);
}
