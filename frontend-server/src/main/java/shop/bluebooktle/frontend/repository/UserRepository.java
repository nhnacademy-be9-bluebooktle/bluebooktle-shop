package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(name = "backend-server", contextId = "userRepository", path = "/api/users", configuration = FeignGlobalConfig.class)
public interface UserRepository {
	@GetMapping("/me")
	@RetryWithTokenRefresh
	UserResponse getMe();

	@PutMapping("/{id}")
	@RetryWithTokenRefresh
	void updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequest userUpdateRequest);
}