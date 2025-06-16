package shop.bluebooktle.backend.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import shop.bluebooktle.backend.user.service.AddressService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;
import shop.bluebooktle.common.security.Auth;
import shop.bluebooktle.common.security.UserPrincipal;

@Slf4j
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "주소 API", description = "사용자 주소 관리 API")
public class AddressController {

	private final AddressService addressService;

	private void checkPrincipal(UserPrincipal userPrincipal) {
		if (userPrincipal == null) {
			throw new InvalidTokenException();
		}
	}

	@Operation(summary = "내 주소 목록 조회", description = "로그인한 사용자의 주소 목록을 조회합니다.")
	@Auth(type = UserType.USER)
	@GetMapping
	public ResponseEntity<JsendResponse<List<AddressResponse>>> getUserAddresses(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		checkPrincipal(userPrincipal);
		List<AddressResponse> addresses = addressService.findAddressesByUserId(userPrincipal.getUserId());
		return ResponseEntity.ok(JsendResponse.success(addresses));
	}

	@Operation(summary = "내 주소 등록", description = "로그인한 사용자의 새 주소를 등록합니다.")
	@Auth(type = UserType.USER)
	@PostMapping
	public ResponseEntity<JsendResponse<AddressResponse>> createAddress(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody AddressRequest request
	) {
		checkPrincipal(userPrincipal);
		AddressResponse createdAddress = addressService.createAddress(userPrincipal.getUserId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(createdAddress));
	}

	@Operation(summary = "내 주소 수정", description = "로그인한 사용자의 특정 주소를 수정합니다.")
	@Auth(type = UserType.USER)
	@PutMapping("/{address-id}")
	public ResponseEntity<JsendResponse<AddressResponse>> updateAddress(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable(name = "address-id") Long addressId,
		@Valid @RequestBody AddressRequest request
	) {
		checkPrincipal(userPrincipal);
		AddressResponse updatedAddress = addressService.updateAddress(userPrincipal.getUserId(), addressId, request);
		return ResponseEntity.ok(JsendResponse.success(updatedAddress));
	}

	@Operation(summary = "내 주소 삭제", description = "로그인한 사용자의 특정 주소를 삭제합니다.")
	@Auth(type = UserType.USER)
	@DeleteMapping("/{address-id}")
	public ResponseEntity<JsendResponse<Void>> deleteAddress(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable(name = "address-id") Long addressId
	) {
		checkPrincipal(userPrincipal);
		addressService.deleteAddress(userPrincipal.getUserId(), addressId);
		return ResponseEntity.ok(JsendResponse.success(null));
	}
}