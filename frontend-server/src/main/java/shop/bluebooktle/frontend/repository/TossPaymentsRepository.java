package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.frontend.config.TossFeignConfig;

@FeignClient(
	name = "tossPaymentsClient",
	url = "https://api.tosspayments.com/v1",
	configuration = TossFeignConfig.class
)
public interface TossPaymentsRepository {

	@PostMapping(value = "/payments/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
	PaymentConfirmResponse confirmPayment(@RequestBody PaymentConfirmRequest request);

}
