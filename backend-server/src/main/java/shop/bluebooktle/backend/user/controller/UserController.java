package shop.bluebooktle.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.user.service.UserService;
import shop.bluebooktle.common.annotation.Auth;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.principal.UserPrincipal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "유저 정보 조회 및 관리 API")
public class UserController {
	private final UserService userService;

	@Operation(summary = "내 유저 정보 조회", description = "내 유저 정보를 가져옵니다. ")
	@Auth(type = UserType.USER)
	@GetMapping("/me")
	public ResponseEntity<JsendResponse<UserResponse>> getMe(
		@Parameter(hidden = true)
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		if (userPrincipal == null) {
			throw new InvalidTokenException();
		}

		try {
			UserResponse userInfo = userService.findByUserId(userPrincipal.getUserId());
			return ResponseEntity.ok(JsendResponse.success(userInfo));
		} catch (UserNotFoundException e) {
			throw new UserNotFoundException();
		}
	}

	@Operation(summary = "내 유저 정보 수정", description = "내 유저 정보를 수정합니다.")
	@Auth(type = UserType.USER)
	@PutMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> updateUser(
		@PathVariable Long id,
		@RequestBody UserUpdateRequest userUpdateRequest
	) {

		try {
			userService.updateUser(id, userUpdateRequest);
			return ResponseEntity.ok(JsendResponse.success(null));
		} catch (UserNotFoundException e) {
			throw new UserNotFoundException();
		}
	}
}