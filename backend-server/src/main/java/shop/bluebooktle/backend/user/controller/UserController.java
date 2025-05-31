package shop.bluebooktle.backend.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.service.UserService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.user.request.AdminUserUpdateRequest;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.dto.user.response.UserTotalPointResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.security.Auth;
import shop.bluebooktle.common.security.UserPrincipal;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "유저 정보 조회 및 관리 API")
public class UserController {
	private final UserService userService;

	@Operation(summary = "회원 목록 조회 (관리자)", description = "페이징 및 검색/필터링을 포함하여 회원 목록을 조회합니다.")
	@GetMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PaginationData<AdminUserResponse>>> listUsers(
		UserSearchRequest request,
		@PageableDefault(size = 10, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
		Page<AdminUserResponse> users = userService.findUsers(request, pageable);
		PaginationData<AdminUserResponse> paginationData = new PaginationData<>(users);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(summary = "회원 상세 조회 (관리자)", description = "특정 회원의 상세 정보를 조회합니다.")
	@GetMapping("/admin/{userId}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<AdminUserResponse>> getUserDetail(
		@PathVariable Long userId) {

		AdminUserResponse user = userService.findUserByIdAdmin(userId);
		return ResponseEntity.ok(JsendResponse.success(user));
	}

	@Operation(summary = "회원 정보 수정 (관리자)", description = "특정 회원의 정보를 수정합니다.")
	@PutMapping("/admin/{userId}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> updateUser(
		@PathVariable Long userId,
		@Valid @RequestBody AdminUserUpdateRequest request) {

		userService.updateUserAdmin(userId, request);
		return ResponseEntity.ok(JsendResponse.success(null));
	}

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

	@Operation(summary = "내 포인트 조회", description = "내 총 포인트를 조회합니다.")
	@Auth(type = UserType.USER)
	@GetMapping("/points")
	public ResponseEntity<JsendResponse<UserTotalPointResponse>> getUserTotalPoints(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		log.info("getUserTotalPoints userId: {}", userPrincipal.getUserId());
		try {
			UserTotalPointResponse userTotalPoints = userService.findUserTotalPoints(userPrincipal.getUserId());
			return ResponseEntity.ok(JsendResponse.success(userTotalPoints));
		} catch (UserNotFoundException e) {
			throw new UserNotFoundException();
		}
	}

	@Operation(summary = "내 정보 및 주소 조회", description = "내 정보 및 주소 ")
	@Auth(type = UserType.USER)
	@GetMapping("/addresses")
	public ResponseEntity<JsendResponse<UserWithAddressResponse>> getUserWithAddresses(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		try {
			UserWithAddressResponse userWithAddress = userService.findUserWithAddress(userPrincipal.getUserId());
			log.info("getUserWithAddresses: {}", userWithAddress);
			return ResponseEntity.ok(JsendResponse.success(userWithAddress));
		} catch (UserNotFoundException e) {
			throw new UserNotFoundException();
		}
	}
}