package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.payment.reponse.TossConfirmResponse;
import shop.bluebooktle.common.dto.payment.request.TossConfirmRequest;
import shop.bluebooktle.frontend.config.TossFeignConfig;

@FeignClient(
	name = "tossPaymentsClient",
	url = "${toss.api.url}",
	configuration = TossFeignConfig.class
)
public interface TossPaymentsRepository {

	@PostMapping(value = "/payments/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
	TossConfirmResponse confirmPayment(@RequestBody TossConfirmRequest request);

}
