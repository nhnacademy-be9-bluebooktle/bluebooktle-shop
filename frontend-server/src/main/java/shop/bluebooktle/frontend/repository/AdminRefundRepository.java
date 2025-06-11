package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.common.dto.refund.response.RefundListResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(
	url = "${server.gateway-url}",
	name = "adminRefundRepository",
	path = "/api/admin/refunds",
	configuration = FeignGlobalConfig.class
)
public interface AdminRefundRepository {
	@PostMapping("/update")
	Void updateRefund(@RequestBody RefundUpdateRequest request);

	@GetMapping
	PaginationData<RefundListResponse> getRefunds(
		@SpringQueryMap RefundSearchRequest request,
		@SpringQueryMap Pageable pageable
	);

	@GetMapping("/{refundId}")
	AdminRefundDetailResponse getRefundDetail(@PathVariable("refundId") Long refundId);
}
