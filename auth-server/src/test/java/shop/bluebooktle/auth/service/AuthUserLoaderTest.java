package shop.bluebooktle.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.auth.repository.UserRepository;
import shop.bluebooktle.auth.service.impl.AuthUserLoaderImpl;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class AuthUserLoaderTest {

	@Mock
	UserRepository userRepository;

	@InjectMocks
	AuthUserLoaderImpl authUserLoader;

	@Test
	@DisplayName("loadUserById - 성공")
	void loadUserById_success() {
		User user = User.builder()
			.id(1L)
			.loginId("testUser")
			.nickname("tester")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		UserPrincipal result = authUserLoader.loadUserById(1L);

		assertThat(result).isNotNull();
		assertThat(result.getUserId()).isEqualTo(1L);
		assertThat(result.getLoginId()).isEqualTo("testUser");
		assertThat(result.getNickname()).isEqualTo("tester");
		assertThat(result.getUserType()).isEqualTo(UserType.USER);
		assertThat(result.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
		assertThat(result.getAuthorities()).hasSize(1);
		assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");

		// UserDetails 관련 메서드 확인
		assertThat(result.getUsername()).isEqualTo("testUser");
		assertThat(result.getPassword()).isNull();
		assertThat(result.isAccountNonExpired()).isTrue();
		assertThat(result.isAccountNonLocked()).isTrue(); // ACTIVE는 true
		assertThat(result.isCredentialsNonExpired()).isTrue();
		assertThat(result.isEnabled()).isTrue(); // ACTIVE만 true
	}

	@Test
	@DisplayName("loadUserById - 실패: 존재하지 않는 사용자")
	void loadUserById_userNotFound() {
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authUserLoader.loadUserById(999L))
			.isInstanceOf(UserNotFoundException.class)
			.hasMessageContaining("사용자를 찾을 수 없습니다");
	}

	@Test
	@DisplayName("loadUserDtoById - 성공")
	void loadUserDtoById_success() {
		User user = User.builder()
			.id(2L)
			.loginId("dtoUser")
			.nickname("dto")
			.type(UserType.USER)
			.status(UserStatus.DORMANT)
			.build();

		when(userRepository.findById(2L)).thenReturn(Optional.of(user));

		UserDto result = authUserLoader.loadUserDtoById(2L);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(2L);
		assertThat(result.getLoginId()).isEqualTo("dtoUser");
		assertThat(result.getNickname()).isEqualTo("dto");
		assertThat(result.getType()).isEqualTo(UserType.USER);
		assertThat(result.getStatus()).isEqualTo(UserStatus.DORMANT);
	}

	@Test
	@DisplayName("loadUserDtoById - 실패: 존재하지 않는 사용자")
	void loadUserDtoById_userNotFound() {
		when(userRepository.findById(888L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authUserLoader.loadUserDtoById(888L))
			.isInstanceOf(UserNotFoundException.class)
			.hasMessageContaining("사용자를 찾을 수 없습니다");
	}
}
