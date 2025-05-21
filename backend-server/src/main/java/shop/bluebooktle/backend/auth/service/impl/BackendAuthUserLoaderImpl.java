package shop.bluebooktle.backend.auth.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.security.UserPrincipal;

@Service("backendAuthUserLoader")
@RequiredArgsConstructor
@Slf4j
public class BackendAuthUserLoaderImpl implements AuthUserLoader {

	private final UserRepository userRepository;

	@Override
	public UserPrincipal loadUserById(Long userId) throws UserNotFoundException {
		try {
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
		} catch (Exception e) {
			throw new UserNotFoundException("AuthServer에서 사용자 정보 조회 중 오류 발생 (ID: " + userId + ")", e);
		}
	}
}