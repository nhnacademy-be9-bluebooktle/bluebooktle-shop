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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book_order.service.PackagingOptionService;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class PackagingOptionController {
	private final PackagingOptionService packagingOptionService;

	/** 포장 옵션 등록 */
	@PostMapping
	public ResponseEntity<JsendResponse<PackagingOptionResponse>> createPackagingOption(
		@RequestBody @Valid PackagingOptionRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(JsendResponse.success(packagingOptionService.createPackagingOption(request)));
	}

	/** 포장 옵션 전체 조회 */
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<PackagingOptionResponse>>> getPackagingOptions(
		@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		Page<PackagingOptionResponse> resultPage = packagingOptionService.getPackagingOption(
			pageable); // 페이징 처리된 응답 객체 가져오기
		PaginationData<PackagingOptionResponse> paginationData = new PaginationData<>(resultPage); // PaginationData 감싸기
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	/** 포장 옵션 수정 */
	@PutMapping("/{packagingOptionId}")
	public ResponseEntity<JsendResponse<PackagingOptionResponse>> updatePackagingOption(
		@PathVariable Long packagingOptionId,
		@RequestBody @Valid PackagingOptionUpdateRequest request) {
		return ResponseEntity.ok(
			JsendResponse.success(packagingOptionService.updatePackagingOption(packagingOptionId, request)));
	}

	/** 포장 옵션 삭제 */
	@DeleteMapping("/{packagingOptionId}")
	public ResponseEntity<JsendResponse<Void>> deletePackagingOption(
		@PathVariable Long packagingOptionId) {
		packagingOptionService.deletePackagingOption(packagingOptionId);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
