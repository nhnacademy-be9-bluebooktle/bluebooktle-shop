package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;

public interface AuthService {
	public void signup(SignupRequest signupRequest);

	public TokenResponse login(LoginRequest loginRequest);

	public TokenResponse refreshToken(TokenRefreshRequest tokenRefreshRequest);
}