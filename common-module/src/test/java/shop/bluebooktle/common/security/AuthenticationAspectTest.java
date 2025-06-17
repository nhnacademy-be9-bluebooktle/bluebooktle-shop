package shop.bluebooktle.common.security;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.exception.auth.HandleAccessDeniedException;
import shop.bluebooktle.common.exception.auth.UnauthoriedException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationAspectTest {

	@InjectMocks
	AuthenticationAspect authenticationAspect;

	@Mock
	JoinPoint joinPoint;

	@Mock
	MethodSignature methodSignature;

	@Mock
	Method method;

	@Mock
	Auth authAnnotation;

	@BeforeEach
	void clearContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("인증 정보 없음 - UnauthoriedException")
	void noAuthentication() {
		SecurityContextHolder.clearContext();
		assertThatThrownBy(() -> authenticationAspect.checkAuthorization(joinPoint))
			.isInstanceOf(UnauthoriedException.class);
	}

	@Test
	@DisplayName("UserPrincipal 아님 - UnauthoriedException")
	void notUserPrincipal() {
		Authentication auth = mock(Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn("notUserPrincipal");

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);

		assertThatThrownBy(() -> authenticationAspect.checkAuthorization(joinPoint))
			.isInstanceOf(UnauthoriedException.class);
	}

	@Test
	@DisplayName("UserType null - HandleAccessDeniedException")
	void userTypeNull() {
		UserPrincipal userPrincipal = mock(UserPrincipal.class);
		when(userPrincipal.getUsername()).thenReturn("nullUser");
		when(userPrincipal.getUserType()).thenReturn(null);

		Authentication auth = mock(Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(userPrincipal);

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);

		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getMethod()).thenReturn(method);
		when(method.getAnnotation(Auth.class)).thenReturn(authAnnotation);
		when(authAnnotation.type()).thenReturn(UserType.USER);

		assertThatThrownBy(() -> authenticationAspect.checkAuthorization(joinPoint))
			.isInstanceOf(HandleAccessDeniedException.class)
			.hasMessageContaining("접근 거부");
	}

	@Test
	@DisplayName("권한 불일치 - HandleAccessDeniedException")
	void userTypeMismatch() {
		UserPrincipal userPrincipal = mock(UserPrincipal.class);
		when(userPrincipal.getUsername()).thenReturn("user");
		when(userPrincipal.getUserType()).thenReturn(UserType.USER);

		Authentication auth = mock(Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(userPrincipal);

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);

		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getMethod()).thenReturn(method);
		when(method.getAnnotation(Auth.class)).thenReturn(authAnnotation);
		when(authAnnotation.type()).thenReturn(UserType.ADMIN);

		assertThatThrownBy(() -> authenticationAspect.checkAuthorization(joinPoint))
			.isInstanceOf(HandleAccessDeniedException.class)
			.hasMessageContaining("요청한 리소스에 접근할 권한이 없습니다");
	}

	@Test
	@DisplayName("비인증 상태 - UnauthoriedException")
	void notAuthenticated() {
		Authentication auth = mock(Authentication.class);
		lenient().when(auth.getPrincipal()).thenReturn(new Object()); // 사용되지 않을 수도 있음
		when(auth.isAuthenticated()).thenReturn(false);

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);

		assertThatThrownBy(() -> authenticationAspect.checkAuthorization(joinPoint))
			.isInstanceOf(UnauthoriedException.class);
	}

	@Test
	@DisplayName("Auth.type()이 null이면 USER로 간주")
	void authTypeNull_defaultsToUser() {
		UserPrincipal userPrincipal = mock(UserPrincipal.class);
		when(userPrincipal.getUsername()).thenReturn("defaultUser");
		when(userPrincipal.getUserType()).thenReturn(UserType.USER);

		Authentication auth = mock(Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(userPrincipal);

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);

		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getMethod()).thenReturn(method);
		when(method.getAnnotation(Auth.class)).thenReturn(authAnnotation);
		lenient().when(authAnnotation.type()).thenReturn(null); // 이 값은 null이므로 호출되지 않을 수도 있음

		authenticationAspect.checkAuthorization(joinPoint); // 통과
	}

	@Test
	@DisplayName("ADMIN이 USER 리소스 접근 허용")
	void adminAccessingUser() {
		UserPrincipal userPrincipal = mock(UserPrincipal.class);
		when(userPrincipal.getUsername()).thenReturn("admin");
		when(userPrincipal.getUserType()).thenReturn(UserType.ADMIN);

		Authentication auth = mock(Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(userPrincipal);

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);

		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getMethod()).thenReturn(method);
		when(method.getAnnotation(Auth.class)).thenReturn(authAnnotation);
		lenient().when(authAnnotation.type()).thenReturn(UserType.USER); // 혹시 호출 안 되더라도 안전하게

		authenticationAspect.checkAuthorization(joinPoint); // 통과
	}

	@Test
	@DisplayName("USER가 USER 리소스 접근 허용")
	void userAccessingUser() {
		UserPrincipal userPrincipal = mock(UserPrincipal.class);
		when(userPrincipal.getUsername()).thenReturn("user");
		when(userPrincipal.getUserType()).thenReturn(UserType.USER);

		Authentication auth = mock(Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(userPrincipal);

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);

		when(joinPoint.getSignature()).thenReturn(methodSignature);
		when(methodSignature.getMethod()).thenReturn(method);
		when(method.getAnnotation(Auth.class)).thenReturn(authAnnotation);
		lenient().when(authAnnotation.type()).thenReturn(UserType.USER);

		authenticationAspect.checkAuthorization(joinPoint); // 통과
	}

}