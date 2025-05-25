package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.PaycoLoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.repository.AuthRepository;
import shop.bluebooktle.frontend.service.AuthService;
import shop.bluebooktle.frontend.util.CookieTokenUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final AuthRepository authRepository;
	private final CookieTokenUtil cookieTokenUtil;

	@Override
	public void signup(SignupRequest signupRequest) {
		authRepository.signup(signupRequest);
	}

	@Override
	public TokenResponse login(HttpServletResponse response, LoginRequest loginRequest) {
		TokenResponse tokenResponse = authRepository.login(loginRequest);

		if (tokenResponse != null && tokenResponse.getAccessToken() != null
			&& tokenResponse.getRefreshToken() != null) {
			cookieTokenUtil.saveTokens(response, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
		}
		return tokenResponse;
	}

	@Override
	public TokenResponse refreshToken(HttpServletResponse response, TokenRefreshRequest tokenRefreshRequest) {
		TokenResponse tokenResponse = authRepository.refreshToken(tokenRefreshRequest);
		cookieTokenUtil.saveTokens(response, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
		return tokenResponse;
	}

	@Override
	public void paycoLogin(HttpServletResponse response, String code) {
		PaycoLoginRequest request = new PaycoLoginRequest(code);

		try {
			TokenResponse tokenResponse = authRepository.paycoLogin(request);

			if (tokenResponse != null && tokenResponse.getAccessToken() != null
				&& tokenResponse.getRefreshToken() != null) {
				cookieTokenUtil.saveTokens(response, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
			} else {
				throw new ApplicationException(ErrorCode.AUTH_OAUTH_LOGIN_FAILED, "PAYCO 로그인 처리 중 오류가 발생했습니다.");
			}
		} catch (Exception e) {
			throw new ApplicationException(ErrorCode.AUTH_OAUTH_LOGIN_FAILED,
				"PAYCO 로그인 중 서버 통신 오류: " + e.getMessage());
		}
	}
}