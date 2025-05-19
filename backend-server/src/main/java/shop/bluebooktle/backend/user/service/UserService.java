package shop.bluebooktle.backend.user.service;

import shop.bluebooktle.common.dto.user.response.UserResponse;

public interface UserService {
	UserResponse findByUserId(Long userId);
}
