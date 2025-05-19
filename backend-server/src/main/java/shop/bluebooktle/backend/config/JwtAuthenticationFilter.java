package shop.bluebooktle.backend.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.principal.UserPrincipal;
import shop.bluebooktle.common.service.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final AuthUserLoader authUserLoader;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		try {
			String jwt = extractJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
				Long userId = jwtUtil.getUserIdFromToken(jwt);
				UserPrincipal userPrincipal = authUserLoader.loadUserById(userId);

				if (userPrincipal != null) {
					UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authentication);
					log.debug("JwtAuthenticationFilter: SecurityContext에 인증 정보 저장 완료. User: {}, URI: {}",
						userPrincipal.getUsername(), request.getRequestURI());
				} else {
					log.info("JwtAuthenticationFilter: 사용자 ID '{}'에 해당하는 사용자를 찾을 수 없습니다. URI: {}", userId,
						request.getRequestURI());
					SecurityContextHolder.clearContext();
				}
			} else {
				if (StringUtils.hasText(jwt)) {
					log.info("JwtAuthenticationFilter: 유효하지 않은 JWT 토큰입니다. URI: {}", request.getRequestURI());
				}
				SecurityContextHolder.clearContext();
			}
		} catch (Exception e) {
			log.info("JwtAuthenticationFilter: JWT 인증 필터 처리 중 오류 발생: {}", e.getMessage(), e);
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}

	private String extractJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}