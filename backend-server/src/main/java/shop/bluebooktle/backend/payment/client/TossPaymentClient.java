package shop.bluebooktle.backend.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import shop.bluebooktle.backend.config.FeignClientLoggingConfig;
import shop.bluebooktle.backend.payment.dto.request.TossApiPaymentConfirmRequest;
import shop.bluebooktle.backend.payment.dto.response.TossApiPaymentConfirmSuccessResponse;

@FeignClient(
	name = "tossPaymentsClient",
	url = "${toss.api.url}",
	configuration = FeignClientLoggingConfig.class
)

public interface TossPaymentClient {
	@PostMapping(value = "/payments/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
	TossApiPaymentConfirmSuccessResponse confirmPayment(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody TossApiPaymentConfirmRequest request
	);
}