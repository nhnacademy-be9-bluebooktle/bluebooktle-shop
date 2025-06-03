package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.user.request.AdminUserUpdateRequest;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.dto.user.response.UserTotalPointResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;

public interface UserService {
	public UserResponse getMe();

	public void updateUser(long id, UserUpdateRequest userUpdateRequest);

	Page<AdminUserResponse> listUsers(UserSearchRequest request, Pageable pageable);

	AdminUserResponse getUserDetail(Long userId);

	void updateUser(Long userId, AdminUserUpdateRequest request);

	UserTotalPointResponse getUserTotalPoints();

	UserWithAddressResponse getUserWithAddresses();
	void withdrawAccount();
}
