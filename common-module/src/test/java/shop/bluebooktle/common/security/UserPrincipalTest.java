package shop.bluebooktle.common.security;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;

class UserPrincipalTest {

	@Test
	@DisplayName("기본 생성자 테스트")
	void defaultConstructor() {
		UserPrincipal principal = new UserPrincipal();
		principal.setUserId(1L);
		principal.setLoginId("loginId");
		principal.setNickname("nickname");
		principal.setUserType(UserType.USER);
		principal.setUserStatus(UserStatus.ACTIVE);
		principal.setAuthorities(null);

		assertThat(principal.getUserId()).isEqualTo(1L);
		assertThat(principal.getLoginId()).isEqualTo("loginId");
		assertThat(principal.getNickname()).isEqualTo("nickname");
		assertThat(principal.getUserType()).isEqualTo(UserType.USER);
		assertThat(principal.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
		assertThat(principal.getAuthorities()).isNull();
	}

	@Test
	@DisplayName("UserDto 기반 생성자 테스트 및 UserDetails 메서드 검증 - 활성 사용자")
	void userDtoConstructor_active() {
		UserDto userDto = new UserDto(
			10L,
			"testLoginId",
			"nick",
			UserType.ADMIN,
			UserStatus.ACTIVE
		);

		UserPrincipal principal = new UserPrincipal(userDto);

		assertThat(principal.getUserId()).isEqualTo(10L);
		assertThat(principal.getLoginId()).isEqualTo("testLoginId");
		assertThat(principal.getNickname()).isEqualTo("nick");
		assertThat(principal.getUserType()).isEqualTo(UserType.ADMIN);
		assertThat(principal.getUserStatus()).isEqualTo(UserStatus.ACTIVE);

		// UserDetails methods
		assertThat(principal.getUsername()).isEqualTo("testLoginId");
		assertThat(principal.getPassword()).isNull();
		assertThat(principal.isAccountNonExpired()).isTrue();
		assertThat(principal.isAccountNonLocked()).isTrue(); // ACTIVE
		assertThat(principal.isCredentialsNonExpired()).isTrue();
		assertThat(principal.isEnabled()).isTrue();

		Collection<?> authorities = principal.getAuthorities();
		assertThat(authorities.iterator().next())
			.isEqualTo(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}

	@Test
	@DisplayName("UserDetails 계정 상태 테스트 - 휴면 사용자")
	void accountStatus_dormant() {
		UserDto userDto = new UserDto(
			20L,
			"dormantId",
			"zzz",
			UserType.USER,
			UserStatus.DORMANT
		);

		UserPrincipal principal = new UserPrincipal(userDto);

		assertThat(principal.isAccountNonLocked()).isFalse(); // DORMANT 상태면 잠김
		assertThat(principal.isEnabled()).isFalse();          // 비활성
	}
}