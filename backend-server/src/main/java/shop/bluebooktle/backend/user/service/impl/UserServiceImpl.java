package shop.bluebooktle.backend.user.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.backend.user.service.UserService;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.user.InvalidUserIdException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	public UserResponse findByUserId(Long userId) {
		if (userId == null) {
			throw new InvalidUserIdException();
		}
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty()) {
			throw new UserNotFoundException();
		}

		return UserResponse.fromEntity(user.get());
	}

	@Override
	public void updateUser(long id, UserUpdateRequest userUpdateRequest) {
		User user = userRepository.findById(id)
			.orElseThrow(UserNotFoundException::new);

		User updatedUser = User.builder()
			.id(id)
			.email(user.getEmail())
			.name(user.getName())
			.loginId(user.getLoginId())
			.phoneNumber(user.getPhoneNumber())
			.type(user.getType())
			.encodedPassword(user.getPassword())
			.membershipLevel(user.getMembershipLevel())
			.status(user.getStatus())
			.lastLoginAt(user.getLastLoginAt())
			.nickname(userUpdateRequest.getNickname())
			.birth(userUpdateRequest.getBirthDate())
			.phoneNumber(userUpdateRequest.getPhoneNumber())
			.build();
		userRepository.save(updatedUser);
	}
}
