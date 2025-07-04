package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", name = "adminPackagingOptionRepository", path = "/api/options", configuration = FeignGlobalConfig.class)
public interface AdminPackagingOptionRepository {
	// 포장 옵션 조회
	@GetMapping
	@RetryWithTokenRefresh
	PaginationData<PackagingOptionInfoResponse> getPackagingOptions(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword);

	// 포장 옵션ID로 단일 조회
	@GetMapping("/{packagingOptionId}")
	@RetryWithTokenRefresh
	PackagingOptionInfoResponse getPackagingOption(@PathVariable("packagingOptionId") long id);

	// 포장 옵션 등록
	@PostMapping
	@RetryWithTokenRefresh
	void createPackagingOption(@RequestBody PackagingOptionInfoResponse request);

	// 포장 옵션 수정
	@PutMapping("/{packagingOptionId}")
	@RetryWithTokenRefresh
	void updatePackagingOption(
		@PathVariable("packagingOptionId") long id,
		@RequestBody PackagingOptionInfoResponse request);

	// 포장 옵션 삭제
	@DeleteMapping("/{packagingOptionId}")
	@RetryWithTokenRefresh
	void deletePackagingOption(@PathVariable("packagingOptionId") long id);
}
