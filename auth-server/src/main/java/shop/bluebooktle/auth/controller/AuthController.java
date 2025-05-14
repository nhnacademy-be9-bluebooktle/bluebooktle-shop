package shop.bluebooktle.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.auth.service.AuthService;
import shop.bluebooktle.common.config.swagger.annotations.ApiAuthenticationFailedResponse;
import shop.bluebooktle.common.config.swagger.annotations.ApiDormantAccountResponse;
import shop.bluebooktle.common.config.swagger.annotations.ApiEmailConflictResponse;
import shop.bluebooktle.common.config.swagger.annotations.ApiInternalServerErrorResponse;
import shop.bluebooktle.common.config.swagger.annotations.ApiLoginIdConflictResponse;
import shop.bluebooktle.common.config.swagger.annotations.ApiLoginSuccessResponse;
import shop.bluebooktle.common.config.swagger.annotations.ApiSignupSuccessResponse;
import shop.bluebooktle.common.config.swagger.annotations.ApiUserNotFoundResponse;
import shop.bluebooktle.common.config.swagger.annotations.ApiValidationFailResponse;
import shop.bluebooktle.common.config.swagger.annotations.ApiWithdrawnAccountResponse;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "사용자 회원가입, 로그인, 토큰 갱신 관련 API")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
	@ApiSignupSuccessResponse
	@ApiLoginIdConflictResponse
	@ApiEmailConflictResponse
	@ApiValidationFailResponse
	@ApiInternalServerErrorResponse
	@PostMapping("/signup")
	public ResponseEntity<JsendResponse<Void>> signup(@Valid @RequestBody SignupRequest signupRequest) {
		authService.signup(signupRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "로그인", description = "사용자 로그인 후 토큰을 발급합니다.")
	@ApiLoginSuccessResponse
	@ApiValidationFailResponse
	@ApiAuthenticationFailedResponse
	@ApiUserNotFoundResponse
	@ApiDormantAccountResponse
	@ApiWithdrawnAccountResponse
	@PostMapping("/login")
	public ResponseEntity<JsendResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
		TokenResponse tokenResponse = authService.login(loginRequest);
		return ResponseEntity.ok(JsendResponse.success(tokenResponse));
	}

	@PostMapping("/refresh")
	public ResponseEntity<JsendResponse<TokenResponse>> refreshToken(
		@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
		TokenResponse tokenResponse = authService.refreshToken(tokenRefreshRequest);
		return ResponseEntity.ok(JsendResponse.success(tokenResponse));
	}
}