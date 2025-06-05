package shop.bluebooktle.backend.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import shop.bluebooktle.common.dto.common.JsendResponse;

@FeignClient(name = "authServerClient", url = "${server.gateway-url}")
public interface AuthServerClient {

	@PostMapping("/auth/logout")
	ResponseEntity<JsendResponse<Void>> logout(@RequestHeader("Authorization") String authorizationHeader);
}