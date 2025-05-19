package shop.bluebooktle.common.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.annotation.Auth;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.principal.UserPrincipal;
import shop.bluebooktle.common.service.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationAspect {

	private final JwtUtil jwtUtil;
	private final AuthUserLoader authUserLoader;

	@Pointcut("@annotation(shop.bluebooktle.common.annotation.Auth)")
	public void authorizedMethod() {
	}

	@Before("authorizedMethod()")
	public void checkAuthenticationAndAuthorization(JoinPoint joinPoint) {
		ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (sra == null) {
			throw new AccessDeniedException("요청 속성을 가져올 수 없습니다. 인증/인가 처리가 불가능합니다.");
		}
		HttpServletRequest request = sra.getRequest();
		String authorizationHeader = request.getHeader("Authorization");
		UserPrincipal userPrincipal = null;

		if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
			String accessToken = authorizationHeader.substring(7);
			try {
				if (!jwtUtil.validateToken(accessToken)) {
					throw new InvalidTokenException("AOP: 제공된 액세스 토큰이 유효하지 않거나 만료되었습니다.");
				}
				Long userId = jwtUtil.getUserIdFromToken(accessToken);

				userPrincipal = authUserLoader.loadUserById(userId);
				if (userPrincipal == null) {
					throw new UserNotFoundException("AOP: 사용자 정보를 찾을 수 없습니다 (ID: " + userId + ")");
				}

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (InvalidTokenException | UserNotFoundException e) {
				SecurityContextHolder.clearContext();
				throw new AccessDeniedException("인증 실패: " + e.getMessage(), e);
			} catch (Exception e) {
				SecurityContextHolder.clearContext();
				throw new AccessDeniedException("토큰 처리 중 오류가 발생했습니다.", e);
			}
		} else {
			SecurityContextHolder.clearContext();
			throw new AccessDeniedException("Bearer 토큰이 필요합니다. 인증되지 않은 접근입니다.");
		}

		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		Auth authAnnotation = method.getAnnotation(Auth.class);

		UserType requiredUserType = authAnnotation.value();
		UserType actualUserType = userPrincipal.getUserType(); // UserPrincipal에서 직접 UserType 가져오기

		boolean authorized = (actualUserType == requiredUserType);
		if (!authorized && actualUserType == UserType.ADMIN && requiredUserType == UserType.USER) {
			authorized = true;
		}

		if (!authorized) {
			throw new AccessDeniedException("요청한 리소스에 접근할 권한이 없습니다. 필요 권한: " + requiredUserType);
		}
	}
}