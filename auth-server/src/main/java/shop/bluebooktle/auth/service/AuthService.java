package shop.bluebooktle.auth.service;

import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.PasswordUpdateRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;

public interface AuthService {
	void signup(SignupRequest signupRequestDto);

	TokenResponse login(LoginRequest loginRequestDto);

	void logout(String accessToken);

	TokenResponse refreshToken(TokenRefreshRequest tokenRefreshRequest);

	void changePassword(Long userId, PasswordUpdateRequest request);

	TokenResponse paycoLogin(String code);
}