package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.frontend.repository.AuthRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements shop.bluebooktle.frontend.service.AuthService {
	private final AuthRepository authRepository;

	@Override
	public void signup(SignupRequest signupRequest) {
		authRepository.signup(signupRequest);

	}

	@Override
	public TokenResponse login(LoginRequest loginRequest) {
		return authRepository.login(loginRequest);
	}

	@Override
	public TokenResponse refreshToken(TokenRefreshRequest tokenRefreshRequest) {
		return authRepository.refreshToken(tokenRefreshRequest);
	}

}