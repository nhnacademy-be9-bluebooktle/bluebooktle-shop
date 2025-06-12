package shop.bluebooktle.common.security;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.exception.auth.HandleAccessDeniedException;
import shop.bluebooktle.common.exception.auth.UnauthoriedException;

@Aspect
@Component
@Slf4j
public class AuthenticationAspect {

	@Pointcut("@annotation(shop.bluebooktle.common.security.Auth)")
	public void authorizedMethod() {
	}

	@Before("authorizedMethod()")
	public void checkAuthorization(
		JoinPoint joinPoint) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
			|| !(authentication.getPrincipal() instanceof UserPrincipal)) {
			log.warn("AuthenticationAspect: SecurityContextHolder에 유효한 인증 정보가 없습니다. Authentication: {}",
				authentication);
			throw new UnauthoriedException();
		}

		UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();

		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		Auth authAnnotation = method.getAnnotation(Auth.class);

		UserType requiredUserType = authAnnotation.type();
		if (requiredUserType == null) {
			requiredUserType = UserType.USER;
		}

		UserType actualUserType = userPrincipal.getUserType();
		if (actualUserType == null) {
			log.error("AuthenticationAspect: 실제 사용자({})의 UserType이 null입니다. UserPrincipal: {}",
				userPrincipal.getUsername(), userPrincipal);
			throw new HandleAccessDeniedException("접근 거부: 사용자 타입 정보를 확인할 수 없습니다.");
		}

		log.debug("AuthenticationAspect: 필요 권한: {}, 실제 권한: {}", requiredUserType, actualUserType);

		boolean authorized = (actualUserType == requiredUserType);
		if (!authorized && actualUserType == UserType.ADMIN && requiredUserType == UserType.USER) {
			authorized = true;
		}

		if (!authorized) {
			log.warn("AuthenticationAspect: 권한 부족. 사용자: {}, 필요 권한: {}, 실제 권한: {}", userPrincipal.getUsername(),
				requiredUserType, actualUserType);
			throw new AccessDeniedException("요청한 리소스에 접근할 권한이 없습니다. 필요 권한: " + requiredUserType);
		}
	}
}