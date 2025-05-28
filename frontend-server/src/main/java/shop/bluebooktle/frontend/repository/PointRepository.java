package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointPolicyResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(
	url = "${server.gateway-url}",
	name = "pointRepository",
	path = "/api/points",
	configuration = FeignGlobalConfig.class
)
public interface PointRepository {

	@PostMapping("/policies")
	Long createPolicy(@RequestBody PointPolicyCreateRequest req);

	@GetMapping("/policies/{id}")
	PointPolicyResponse getPolicy(@PathVariable("id") Long id);

	@PutMapping("/policies")
	Void updatePolicy(@RequestBody PointPolicyUpdateRequest req);

	@DeleteMapping("/policies/{id}")
	Void deletePolicy(@PathVariable("id") Long id);

	@GetMapping("/policies")
	List<PointPolicyResponse> getAllPolicies();

	@GetMapping("/rules")
	List<PointRuleResponse> getAllRules();

	@PostMapping("/source")
	Long createSourceType(@RequestBody PointSourceTypeCreateRequest req);

	@GetMapping("/source/{id}")
	PointSourceTypeResponse getSourceType(@PathVariable("id") Long id);

	@GetMapping("/source")
	List<PointSourceTypeResponse> getAllSourceTypes(
		@RequestParam(value = "actionType", required = false) String actionType);

	@DeleteMapping("/source/{id}")
	Void deleteSourceType(@PathVariable("id") Long id);

}
