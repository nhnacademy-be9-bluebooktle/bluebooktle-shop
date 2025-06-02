package shop.bluebooktle.common.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;

@Getter
@Setter
public class UserPrincipal implements UserDetails {

	private Long userId;
	private String loginId;
	private String nickname;
	private UserType userType;
	private UserStatus userStatus;
	private Collection<? extends GrantedAuthority> authorities;

	public UserPrincipal() {
	}

	public UserPrincipal(UserDto userDto) {
		this.userId = userDto.getId();
		this.loginId = userDto.getLoginId();
		this.nickname = userDto.getNickname();
		this.userType = userDto.getType();
		this.userStatus = userDto.getStatus();
		this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userDto.getType().name()));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.loginId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.userStatus != UserStatus.DORMANT;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.userStatus == UserStatus.ACTIVE;
	}
}
