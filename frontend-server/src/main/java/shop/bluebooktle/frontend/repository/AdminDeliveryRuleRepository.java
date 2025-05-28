package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleUpdateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(
	url = "${server.gateway-url}",
	path = "/api/admin/delivery-rules",
	name = "adminDeliveryRuleRepository",
	configuration = FeignGlobalConfig.class
)
public interface AdminDeliveryRuleRepository {

	@GetMapping("/{id}")
	DeliveryRuleResponse getById(@PathVariable("id") Long id);

	@GetMapping
	PaginationData<DeliveryRuleResponse> getAll(
		@RequestParam("page") int page,
		@RequestParam("size") int size
	);

	@GetMapping("/active")
	PaginationData<DeliveryRuleResponse> getActive(
		@RequestParam("page") int page,
		@RequestParam("size") int size
	);

	@PostMapping
	Long create(@RequestBody DeliveryRuleCreateRequest request);

	@PutMapping("/{id}")
	Void update(
		@PathVariable("id") Long id,
		@RequestBody DeliveryRuleUpdateRequest request
	);

	@DeleteMapping("/{id}")
	Void delete(@PathVariable("id") Long id);
}
