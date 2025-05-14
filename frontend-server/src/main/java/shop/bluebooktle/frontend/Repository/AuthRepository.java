package shop.bluebooktle.frontend.Repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@FeignClient(name = "auth-server", path = "/auth")
public interface AuthRepository {
	
	@PostMapping("/signup")
	ResponseEntity<JsendResponse<Void>> signup(@RequestBody SignupRequest signupRequest);

	@PostMapping("/login")
	ResponseEntity<JsendResponse<TokenResponse>> login(@RequestBody LoginRequest loginRequest);

	@PostMapping("/refresh")
	ResponseEntity<JsendResponse<TokenResponse>> refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest);
}