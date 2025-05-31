package shop.bluebooktle.backend.user.service;

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
	UserResponse findByUserId(Long userId);

	void updateUser(long id, UserUpdateRequest userUpdateRequest);

	Page<AdminUserResponse> findUsers(UserSearchRequest request, Pageable pageable);

	AdminUserResponse findUserByIdAdmin(Long userId);

	void updateUserAdmin(Long userId, AdminUserUpdateRequest request);

	UserTotalPointResponse findUserTotalPoints(Long userId);

	UserWithAddressResponse findUserWithAddress(Long userId);
}
