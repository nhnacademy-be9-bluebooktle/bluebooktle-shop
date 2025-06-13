package shop.bluebooktle.backend.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.security.UserPrincipal;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		UserDto userDto = UserDto.builder()
			.id(withMockCustomUser.userId())
			.loginId(withMockCustomUser.loginId())
			.nickname(withMockCustomUser.nickname())
			.type(withMockCustomUser.userType())
			.status(withMockCustomUser.userStatus())
			.build();

		UserPrincipal principal = new UserPrincipal(userDto);

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

		context.setAuthentication(authentication);
		return context;
	}
}
