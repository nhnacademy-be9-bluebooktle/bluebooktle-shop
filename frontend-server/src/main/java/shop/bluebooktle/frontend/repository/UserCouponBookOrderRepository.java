package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book_order.request.UserCouponBookOrderRequest;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", path = "/api/user-coupons/usages", name = "userCouponBookOrderRepository", configuration = FeignGlobalConfig.class)
public interface UserCouponBookOrderRepository {
	@PostMapping
	void saveUsage(@RequestBody UserCouponBookOrderRequest request);

	@DeleteMapping
	void deleteUsage(
		@RequestParam("orderId") Long orderId,
		@RequestParam("userCouponIds") List<Long> userCouponIds
	);
}
