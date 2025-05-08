package shop.bluebooktle.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.auth.service.AuthService;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<JsendResponse<Void>> signup(@Valid @RequestBody SignupRequest signupRequest) {
		authService.signup(signupRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

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