package shop.bluebooktle.common.security; // 패키지 경로는 실제 프로젝트에 맞게 조정하세요.

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.common.util.JwtUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final AuthUserLoader authUserLoader;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String jwt = null;
		try {
			jwt = extractJwtFromRequest(request);

			if (StringUtils.hasText(jwt)) {
				if (jwtUtil.validateToken(jwt)) {
					Long userId = jwtUtil.getUserIdFromToken(jwt);
					UserPrincipal userPrincipal = authUserLoader.loadUserById(userId);

					if (userPrincipal != null) {
						UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(userPrincipal, null,
								userPrincipal.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(authentication);
						log.debug("JwtAuthenticationFilter: SecurityContext에 인증 정보 저장 완료. User: {}, URI: {}",
							userPrincipal.getUsername(), request.getRequestURI());
					} else {
						sendErrorResponse(response, ErrorCode.AUTH_USER_NOT_FOUND, "토큰에 해당하는 사용자를 찾을 수 없습니다.");
						return;
					}
				} else {
					log.warn(
						"JwtAuthenticationFilter: jwtUtil.validateToken(jwt)이 false를 반환했습니다. 유효하지 않은 토큰으로 간주합니다. URI: {}",
						request.getRequestURI());
					sendErrorResponse(response, ErrorCode.AUTH_TOKEN_VALIDATION_FAILED, "유효하지 않은 토큰입니다 (검증 실패).");
					return;
				}
			}
			filterChain.doFilter(request, response);

		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			log.info("JwtAuthenticationFilter: 만료된 JWT 토큰입니다. URI: {}", request.getRequestURI());
			SecurityContextHolder.clearContext();
			sendErrorResponse(response, ErrorCode.UNAUTHORIZED, "만료된 토큰입니다. 다시 로그인해주세요.");
		} catch (io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.security.SignatureException |
				 io.jsonwebtoken.UnsupportedJwtException | IllegalArgumentException e) {
			log.info("JwtAuthenticationFilter: 유효하지 않은 형식/서명/지원되지 않는 JWT 토큰입니다. {} - URI: {}", e.getMessage(),
				request.getRequestURI());
			SecurityContextHolder.clearContext();
			sendErrorResponse(response, ErrorCode.AUTH_TOKEN_VALIDATION_FAILED, "유효하지 않은 토큰입니다: " + e.getMessage());
		} catch (Exception e) {
			log.error("JwtAuthenticationFilter: JWT 인증 필터 처리 중 예측하지 못한 오류 발생: {}", e.getMessage(), e);
			SecurityContextHolder.clearContext();
			if (!response.isCommitted()) {
				sendErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, "인증 처리 중 서버 내부 오류가 발생했습니다.");
			}
		}
	}

	private String extractJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode, String customMessage) throws
		IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		String message = StringUtils.hasText(customMessage) ? customMessage : errorCode.getMessage();
		JsendResponse<Object> errorResponse = JsendResponse.error(message, errorCode.getCode(), null);

		objectMapper.writeValue(response.getWriter(), errorResponse);
	}
}