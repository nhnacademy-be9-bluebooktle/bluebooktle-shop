package shop.bluebooktle.common.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import shop.bluebooktle.common.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtAuthenticationFilterTest {

	@InjectMocks
	JwtAuthenticationFilter filter;

	@Mock
	JwtUtil jwtUtil;
	@Mock
	AuthUserLoader authUserLoader;
	@Mock
	ObjectMapper objectMapper;
	@Mock
	HttpServletRequest request;
	@Mock
	FilterChain chain;

	MockHttpServletResponse response;

	@BeforeEach
	void setup() {
		response = new MockHttpServletResponse();
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("Authorization 헤더 없음 - 필터 체인 통과")
	void noAuthorizationHeader() throws Exception {
		when(request.getHeader("Authorization")).thenReturn(null);

		filter.doFilterInternal(request, response, chain);

		verify(chain).doFilter(request, response);
		assertThat(response.getStatus()).isEqualTo(200); // default
	}

	@Test
	@DisplayName("토큰 유효하지 않음 - 401 응답")
	void invalidToken() throws Exception {
		when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
		when(jwtUtil.validateToken("invalid.token")).thenReturn(false);
		when(request.getRequestURI()).thenReturn("/test");

		filter.doFilterInternal(request, response, chain);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	@DisplayName("유효한 토큰 + 사용자 존재 - 인증 성공 및 필터 체인 통과")
	void validToken_withUser() throws Exception {
		when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
		when(jwtUtil.validateToken("valid.token")).thenReturn(true);
		when(jwtUtil.getUserIdFromToken("valid.token")).thenReturn(123L);
		UserPrincipal userPrincipal = mock(UserPrincipal.class);
		when(userPrincipal.getUsername()).thenReturn("testUser");
		when(authUserLoader.loadUserById(123L)).thenReturn(userPrincipal);
		when(request.getRequestURI()).thenReturn("/test");

		filter.doFilterInternal(request, response, chain);

		verify(chain).doFilter(request, response);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
	}

	@Test
	@DisplayName("유효한 토큰 + 사용자 없음 - 404 응답")
	void validToken_butUserNotFound() throws Exception {
		when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
		when(jwtUtil.validateToken("valid.token")).thenReturn(true);
		when(jwtUtil.getUserIdFromToken("valid.token")).thenReturn(999L);
		when(authUserLoader.loadUserById(999L)).thenReturn(null);

		filter.doFilterInternal(request, response, chain);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@Test
	@DisplayName("토큰 만료 - ExpiredJwtException - 401 응답")
	void expiredToken() throws Exception {
		when(request.getHeader("Authorization")).thenReturn("Bearer expired.token");
		when(jwtUtil.validateToken("expired.token")).thenThrow(new ExpiredJwtException(null, null, "expired"));
		when(request.getRequestURI()).thenReturn("/expired");

		filter.doFilterInternal(request, response, chain);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	@DisplayName("MalformedJwtException - 401 응답")
	void malformedToken() throws Exception {
		when(request.getHeader("Authorization")).thenReturn("Bearer malformed.token");
		when(jwtUtil.validateToken("malformed.token")).thenThrow(new MalformedJwtException("bad format"));
		when(request.getRequestURI()).thenReturn("/bad");

		filter.doFilterInternal(request, response, chain);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	@DisplayName("Unexpected 예외 발생 - 500 응답")
	void unexpectedException() throws Exception {
		when(request.getHeader("Authorization")).thenReturn("Bearer crash.token");
		when(jwtUtil.validateToken("crash.token")).thenThrow(new RuntimeException("Boom!"));

		filter.doFilterInternal(request, response, chain);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
}