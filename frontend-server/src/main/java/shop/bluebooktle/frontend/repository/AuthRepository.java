package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.PasswordUpdateRequest;
import shop.bluebooktle.common.dto.auth.request.PaycoLoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", name = "authRepository", path = "/auth", configuration = FeignGlobalConfig.class)
public interface AuthRepository {
	@PostMapping("/signup")
	void signup(@RequestBody SignupRequest signupRequest);

	@PostMapping("/login")
	TokenResponse login(@RequestBody LoginRequest loginRequest);

	@PostMapping("/refresh")
	TokenResponse refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest);

	@PostMapping("/logout")
	void logout();

	@PostMapping("/payco")
	TokenResponse paycoLogin(@RequestBody PaycoLoginRequest paycoLoginRequest);

	@PostMapping("/password/change")
	void changePassword(@RequestBody PasswordUpdateRequest request);
}
