package shop.bluebooktle.auth.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import shop.bluebooktle.auth.service.AuthService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.PasswordUpdateRequest;
import shop.bluebooktle.common.dto.auth.request.PaycoLoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	AuthService authService;

	@InjectMocks
	AuthController authController;

	UserPrincipal principal;

	@BeforeEach
	void setup() {
		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		principal = new UserPrincipal(userDto);
	}

	@Test
	@DisplayName("회원가입 성공")
	void signup_success() {
		SignupRequest request = new SignupRequest();
		request.setLoginId("validuser");
		request.setPassword("ValidPass123");
		request.setName("홍길동");
		request.setEmail("test@example.com");
		request.setNickname("길동이");
		request.setBirth("19990101");
		request.setPhoneNumber("01012345678");

		ResponseEntity<JsendResponse<Void>> response = authController.signup(request);

		assertThat(response.getStatusCodeValue()).isEqualTo(201);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().status()).isEqualTo("success");
		verify(authService).signup(request);
	}

	@Test
	@DisplayName("로그인 성공")
	void login_success() {
		LoginRequest request = new LoginRequest();
		request.setLoginId("loginId");
		request.setPassword("password");

		TokenResponse tokenResponse = new TokenResponse("access", "refresh");
		when(authService.login(request)).thenReturn(tokenResponse);

		ResponseEntity<JsendResponse<TokenResponse>> response = authController.login(request);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(Objects.requireNonNull(response.getBody()).data()).isEqualTo(tokenResponse);
		verify(authService).login(request);
	}

	@Test
	@DisplayName("토큰 재발급 성공")
	void refreshToken_success() {
		TokenRefreshRequest request = new TokenRefreshRequest("refresh-token");
		TokenResponse responseToken = new TokenResponse("new-access", "new-refresh");
		when(authService.refreshToken(request)).thenReturn(responseToken);

		ResponseEntity<JsendResponse<TokenResponse>> response = authController.refreshToken(request);

		assertThat(Objects.requireNonNull(response.getBody()).data()).isEqualTo(responseToken);
		verify(authService).refreshToken(request);
	}

	@Test
	@DisplayName("로그아웃 성공")
	void logout_success() {
		String token = "access-token";

		Authentication authentication = mock(Authentication.class);
		when(authentication.getCredentials()).thenReturn(token);

		ResponseEntity<JsendResponse<Void>> response = authController.logout(authentication);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(Objects.requireNonNull(response.getBody()).status()).isEqualTo("success");
		verify(authService).logout(token);
	}

	@Test
	@DisplayName("비밀번호 변경 성공")
	void changePassword_success() {
		PasswordUpdateRequest request = new PasswordUpdateRequest();
		request.setCurrentPassword("current123");
		request.setNewPassword("newPassword456");
		request.setConfirmNewPassword("newPassword456");

		ResponseEntity<JsendResponse<Void>> response = authController.changePassword(principal, request);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(Objects.requireNonNull(response.getBody()).status()).isEqualTo("success");
		verify(authService).changePassword(1L, request);
	}

	@Test
	@DisplayName("페이코 로그인 성공")
	void paycoLogin_success() {
		PaycoLoginRequest request = new PaycoLoginRequest("code123");
		TokenResponse tokenResponse = new TokenResponse("access", "refresh");

		when(authService.paycoLogin("code123")).thenReturn(tokenResponse);

		ResponseEntity<JsendResponse<TokenResponse>> response = authController.paycoLogin(request);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(Objects.requireNonNull(response.getBody()).data()).isEqualTo(tokenResponse);
		verify(authService).paycoLogin("code123");
	}
}
