package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import shop.bluebooktle.common.dto.admin.DashboardStatusResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", name = "dashboardRepository", path = "/api/admin/dashboard", configuration = FeignGlobalConfig.class)
public interface DashboardRepository {

	@GetMapping("/status")
	@RetryWithTokenRefresh
	DashboardStatusResponse getDashboardStatus();
}