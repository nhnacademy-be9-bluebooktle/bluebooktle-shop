package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.UserResponse;

public interface UserService {
	public UserResponse getMe();

	public void updateUser(long id, UserUpdateRequest userUpdateRequest);
}
