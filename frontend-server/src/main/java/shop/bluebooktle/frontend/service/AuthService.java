package shop.bluebooktle.frontend.service;

import jakarta.servlet.http.HttpServletResponse;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.PasswordUpdateRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;

public interface AuthService {
	public void signup(SignupRequest signupRequest);

	public TokenResponse login(HttpServletResponse response, LoginRequest loginRequest);

	TokenResponse refreshToken(HttpServletResponse response, TokenRefreshRequest tokenRefreshRequest);

	public void changePassword(PasswordUpdateRequest loginRequest);

	void logout(HttpServletResponse response);

	public void paycoLogin(HttpServletResponse response, String code);
}