package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", name = "MembershipLevelRepository", path = "/api/memberships", configuration = FeignGlobalConfig.class)
public interface MembershipLevelRepository {
	@GetMapping
	List<MembershipLevelDetailDto> getMembershipLevels();
}
