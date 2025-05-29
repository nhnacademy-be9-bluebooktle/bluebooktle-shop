package shop.bluebooktle.backend.point.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.point.service.PointService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.common.security.Auth;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@Tag(name = "포인트 API", description = "포인트 이력 및 적립 API")
public class PointHistoryController {

	private final PointService pointHistoryService;

	@Operation(summary = "포인트 이력 조회", description = "로그인한 유저의 포인트 이력을 페이징하여 조회합니다.")
	@Auth(type = UserType.USER)
	@GetMapping("/history")
	public ResponseEntity<JsendResponse<PaginationData<PointHistoryResponse>>> getMyPointHistories(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
		@PageableDefault(size = 10, sort = "createdAt") Pageable pageable
	) {
		Page<PointHistoryResponse> pointHistories = pointHistoryService.getPointHistoriesByUserId(
			userPrincipal.getUserId(), pageable);
		PaginationData<PointHistoryResponse> paginationData = new PaginationData<>(pointHistories);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(summary = "포인트 적립/사용 등록", description = "현재 로그인한 유저의 포인트 이력을 생성합니다.")
	@Auth(type = UserType.USER)
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> createPointHistory(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody PointAdjustmentRequest request
	) {
		pointHistoryService.savePointHistory(userPrincipal.getUserId(), request);
		return ResponseEntity.ok(JsendResponse.success(null));
	}
}
