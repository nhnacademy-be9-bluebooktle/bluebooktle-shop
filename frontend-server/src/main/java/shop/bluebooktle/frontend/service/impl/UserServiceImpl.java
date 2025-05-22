package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;
import shop.bluebooktle.frontend.repository.UserRepository;
import shop.bluebooktle.frontend.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	@RetryWithTokenRefresh
	public UserResponse getMe() {
		return userRepository.getMe();
	}

	@Override
	@RetryWithTokenRefresh
	public void updateUser(long id, UserUpdateRequest userUpdateRequest) {
		userRepository.updateUser(id, userUpdateRequest);
	}
}
