package shop.bluebooktle.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.auth.service.impl.AuthUserLoaderImpl;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

	private final AuthUserLoaderImpl userDetailLoaderService;

	@GetMapping("/{userId}")
	public ResponseEntity<JsendResponse<UserDto>> getUserDetails(@PathVariable Long userId) {
		UserDto userDto = userDetailLoaderService.loadUserDtoById(userId);
		return ResponseEntity.ok(JsendResponse.success(userDto));
	}
}