package shop.bluebooktle.backend.user.service;

import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.UserResponse;

public interface UserService {
	UserResponse findByUserId(Long userId);

	void updateUser(long id, UserUpdateRequest userUpdateRequest);
}
