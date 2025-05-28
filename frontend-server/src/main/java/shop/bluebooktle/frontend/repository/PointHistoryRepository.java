package shop.bluebooktle.frontend.repository;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(
	name = "backend-server",
	contextId = "pointHistoryRepository",
	path = "/api/points",
	configuration = FeignGlobalConfig.class
)
public interface PointHistoryRepository {

	@GetMapping("/history")
	PaginationData<PointHistoryResponse> getMyPointHistories(Pageable pageable);

	@PostMapping
	Void createPointHistory(@RequestBody PointAdjustmentRequest request);

	@GetMapping("/total")
	BigDecimal getTotalPoints();
}
