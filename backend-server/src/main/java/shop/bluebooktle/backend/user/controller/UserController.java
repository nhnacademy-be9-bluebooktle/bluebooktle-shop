package shop.bluebooktle.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.service.UserService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.principal.UserPrincipal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;

	public ResponseEntity<JsendResponse<UserResponse>> getMyInfo(
		@Parameter(hidden = true)
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		if (userPrincipal == null) {
			throw new InvalidTokenException("Authentication failed");

		}
		Long currentUserId = userPrincipal.getUserId();
		try {
			UserResponse userInfo = userService.findByUserId(currentUserId);
			return ResponseEntity.ok(JsendResponse.success(userInfo));
		} catch (UserNotFoundException e) {
			log.warn("사용자 ID {}에 해당하는 사용자를 찾을 수 없습니다 (내 정보 조회).", currentUserId, e);
			throw new UserNotFoundException();
		}
	}
}
