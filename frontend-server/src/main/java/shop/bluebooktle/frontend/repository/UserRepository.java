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
import shop.bluebooktle.common.dto.user.request.AdminUserUpdateRequest;
import shop.bluebooktle.common.dto.user.request.IssueDormantAuthCodeRequest;
import shop.bluebooktle.common.dto.user.request.ReactivateDormantUserRequest;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;
import shop.bluebooktle.common.dto.user.response.UserMembershipLevelResponse;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.dto.user.response.UserTotalPointResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", name = "userRepository", path = "/api/users", configuration = FeignGlobalConfig.class)
public interface UserRepository {
	@GetMapping("/me")
	@RetryWithTokenRefresh
	UserResponse getMe();

	@PutMapping("/{id}")
	@RetryWithTokenRefresh
	void updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequest userUpdateRequest);

	@GetMapping
	@RetryWithTokenRefresh
	PaginationData<AdminUserResponse> listUsers(
		@RequestParam(value = "searchField", required = false) String searchField,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@RequestParam(value = "userTypeFilter", required = false) String userTypeFilter,
		@RequestParam(value = "userStatusFilter", required = false) String userStatusFilter,
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam("sort") String sort
	);

	@GetMapping("/admin/{userId}")
	@RetryWithTokenRefresh
	AdminUserResponse getUserDetail(@PathVariable("userId") Long userId);

	@PutMapping("/admin/{userId}")
	@RetryWithTokenRefresh
	Void updateUser(
		@PathVariable("userId") Long userId,
		@RequestBody AdminUserUpdateRequest request
	);

	@GetMapping("/points")
	UserTotalPointResponse getUserTotalPoints();

	@GetMapping("/membership")
	UserMembershipLevelResponse getUserMembership();

	@GetMapping("/addresses")
	UserWithAddressResponse getUserWithAddresses();

	@DeleteMapping("/me")
	@RetryWithTokenRefresh
	void withdrawMyAccount();

	@PostMapping("/dormant/issue-code")
	@RetryWithTokenRefresh
	void requestDormantAuthCode(@RequestBody IssueDormantAuthCodeRequest request);

	@PostMapping("/dormant/reactivate")
	@RetryWithTokenRefresh
	void reactivateDormantUser(@RequestBody ReactivateDormantUserRequest request);
}