package shop.bluebooktle.auth.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.auth.repository.UserRepository;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.principal.UserPrincipal;
import shop.bluebooktle.common.service.AuthUserLoader;

@Service("authServerAuthUserLoader")
@RequiredArgsConstructor
public class AuthUserLoaderImpl implements AuthUserLoader {
	private final UserRepository userRepository;

	@Override
	public UserPrincipal loadUserById(Long userId) throws UserNotFoundException {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다 (ID: " + userId + ")"));
		UserDto userDto = UserDto.builder()
			.id(user.getId())
			.loginId(user.getLoginId())
			.nickname(user.getNickname())
			.type(user.getType())
			.status(user.getStatus())
			.build();
		return new UserPrincipal(userDto);
	}

	public UserDto loadUserDtoById(Long userId) throws UserNotFoundException {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다 (ID: " + userId + ")"));
		return UserDto.builder()
			.id(user.getId())
			.loginId(user.getLoginId())
			.nickname(user.getNickname())
			.type(user.getType())
			.status(user.getStatus())
			.build();
	}
}