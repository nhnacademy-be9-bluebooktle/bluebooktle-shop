package shop.bluebooktle.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.auth.service.AuthService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.PasswordUpdateRequest;
import shop.bluebooktle.common.dto.auth.request.PaycoLoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.security.Auth;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "사용자 회원가입, 로그인, 토큰 갱신 관련 API")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
	@PostMapping("/signup")
	public ResponseEntity<JsendResponse<Void>> signup(@Valid @RequestBody SignupRequest signupRequest) {
		authService.signup(signupRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "로그인", description = "사용자 로그인 후 토큰을 발급합니다.")
	@PostMapping("/login")
	public ResponseEntity<JsendResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
		TokenResponse tokenResponse = authService.login(loginRequest);
		return ResponseEntity.ok(JsendResponse.success(tokenResponse));
	}

	@Operation(summary = "토큰 재발급", description = "재발급 토큰을 사용해 토큰을 재발급합니다.")
	@PostMapping("/refresh")
	public ResponseEntity<JsendResponse<TokenResponse>> refreshToken(
		@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
		TokenResponse tokenResponse = authService.refreshToken(tokenRefreshRequest);
		return ResponseEntity.ok(JsendResponse.success(tokenResponse));
	}

	@Operation(summary = "로그아웃", description = "사용자 AccessToken과 RefreshToken을 만료합니다.")
	@PostMapping("/logout")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<Void>> logout(
		Authentication authentication) {
		String accessToken = (String)authentication.getCredentials();
		authService.logout(accessToken);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "비밀번호 변경", description = "로그인된 사용자의 비밀번호를 변경합니다.")
	@PostMapping("/password/change")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<Void>> changePassword(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody PasswordUpdateRequest request) {

		Long userId = userPrincipal.getUserId();

		authService.changePassword(userId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "페이코 로그인", description = "페이코 인가 코드를 받아 로그인/회원가입 처리 후 토큰을 발급합니다.")
	@PostMapping("/payco")
	public ResponseEntity<JsendResponse<TokenResponse>> paycoLogin(@RequestBody PaycoLoginRequest paycoLoginRequest) {
		TokenResponse tokenResponse = authService.paycoLogin(paycoLoginRequest.getCode());
		return ResponseEntity.ok(JsendResponse.success(tokenResponse));
	}
}