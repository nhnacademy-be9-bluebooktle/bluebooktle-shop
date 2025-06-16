package shop.bluebooktle.backend.point.controller;

import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.point.service.PointPolicyService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.response.PointPolicyResponse;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points/policies")
@Tag(name = "포인트 정책 API", description = "포인트 정책 추가, 삭제 관련 API")
public class PointPolicyController {

	private final PointPolicyService pointPolicyService;

	@Operation(summary = "포인트 정책 생성", description = "포인트 정책을 생성합니다.")
	@PostMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Long>> createPointPolicy(@Valid @RequestBody PointPolicyCreateRequest request) {
		Long id = pointPolicyService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(id));
	}

	@Operation(summary = "포인트 정책 조회", description = "포인트 정책을 조회합니다.")
	@GetMapping("/{point-policy-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PointPolicyResponse>> getPointPolicy(
		@PathVariable(name = "point-policy-id") Long pointPolicyId
	) {
		PointPolicyResponse response = pointPolicyService.get(pointPolicyId);
		return ResponseEntity.ok(JsendResponse.success(response));
	}

	@Operation(summary = "포인트 정책 업데이트", description = "포인트 정책을 갱신합니다.")
	@PutMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> updatePointPolicy(@Valid @RequestBody PointPolicyUpdateRequest request) {
		pointPolicyService.update(request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "포인트 정책 삭제", description = "포인트 정책을 삭제합니다.")
	@DeleteMapping("/{point-policy-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> deletePointPolicy(
		@PathVariable(name = "point-policy-id") Long pointPolicyId
	) {
		pointPolicyService.delete(pointPolicyId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "포인트 정책 전체 조회", description = "포인트 정책 전체 조회.")
	@GetMapping
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<List<PointPolicyResponse>>> getAllPointPolicies() {
		List<PointPolicyResponse> response = pointPolicyService.findAll();
		return ResponseEntity.ok(JsendResponse.success(response));
	}
}
