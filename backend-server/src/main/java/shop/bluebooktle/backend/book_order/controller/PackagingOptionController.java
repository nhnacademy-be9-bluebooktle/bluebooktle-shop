package shop.bluebooktle.backend.book_order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book_order.service.PackagingOptionService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
@Tag(name = "포장 옵션 API", description = "포장 옵션 조회 및 관리합니다.")
public class PackagingOptionController {
	private final PackagingOptionService packagingOptionService;
	
	@Operation(summary = "포장 옵션 등록", description = "포장 옵션을 등록합니다.")
	@PostMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PackagingOptionInfoResponse>> createPackagingOption(
		@RequestBody @Valid PackagingOptionRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(JsendResponse.success(packagingOptionService.createPackagingOption(request)));
	}

	@Operation(summary = "포장 옵션 목록 조회", description = "포장 옵션 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<PackagingOptionInfoResponse>>> getPackagingOptions(
		@PageableDefault(size = 10, sort = "id") Pageable pageable,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

		Page<PackagingOptionInfoResponse> packagingOptionPage;

		if (searchKeyword != null && !searchKeyword.isBlank()) {
			packagingOptionPage = packagingOptionService.searchPackagingOption(searchKeyword, pageable);
		} else {
			packagingOptionPage = packagingOptionService.getPackagingOptions(pageable);
		}

		PaginationData<PackagingOptionInfoResponse> paginationData = new PaginationData<>(packagingOptionPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(summary = "포장 옵션 조회", description = "포장 옵션을 조회합니다.")
	@GetMapping("/{packaging-option-id}")
	public ResponseEntity<JsendResponse<PackagingOptionInfoResponse>> getPackagingOption(
		@PathVariable(name = "packaging-option-id") Long packagingOptionId) {
		PackagingOptionInfoResponse response = packagingOptionService.getPackagingOption(packagingOptionId);
		return ResponseEntity.ok(JsendResponse.success(response));
	}

	@Operation(summary = "포장 옵션 수정", description = "포장 옵션을 수정합니다.")
	@PutMapping("/{packaging-option-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PackagingOptionInfoResponse>> updatePackagingOption(
		@PathVariable(name = "packaging-option-id") Long packagingOptionId,
		@RequestBody @Valid PackagingOptionRequest request) {
		return ResponseEntity.ok(
			JsendResponse.success(packagingOptionService.updatePackagingOption(packagingOptionId, request)));
	}

	@Operation(summary = "포장 옵션 삭제", description = "포장 옵션을 삭제합니다.")
	@DeleteMapping("/{packaging-option-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> deletePackagingOption(
		@PathVariable(name = "packaging-option-id") Long packagingOptionId) {
		packagingOptionService.deletePackagingOption(packagingOptionId);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
