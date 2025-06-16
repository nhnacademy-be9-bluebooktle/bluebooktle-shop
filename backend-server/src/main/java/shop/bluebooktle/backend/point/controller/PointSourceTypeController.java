package shop.bluebooktle.backend.point.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.point.service.PointSourceTypeService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points/source")
@Tag(name = "포인트 발생 유형 API", description = "포인트 발생 유형 추가, 삭제 관련 API")
public class PointSourceTypeController {

	private final PointSourceTypeService pointSourceTypeService;

	@Operation(summary = "포인트 발생 유형 생성", description = "포인트 발생 유형을 생성합니다.")
	@PostMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Long>> createSource(
		@Valid @RequestBody PointSourceTypeCreateRequest request) {
		Long id = pointSourceTypeService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(id));
	}

	@Operation(summary = "단건 조회", description = "포인트 발생 유형을 조회합니다.")
	@GetMapping("/{point-source-type-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PointSourceTypeResponse>> getSource(
		@PathVariable(name = "point-source-type-id") Long pointSourceTypeId
	) {
		PointSourceTypeResponse resp = pointSourceTypeService.get(pointSourceTypeId);
		return ResponseEntity.ok(JsendResponse.success(resp));
	}

	@Operation(summary = "포인트 발생 유형 조회", description = " 전체 또는 특정 ActionType별 조회합니다.")
	@GetMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<List<PointSourceTypeResponse>>> getSourceList(
		@RequestParam(required = false) String actionType) {

		List<PointSourceTypeResponse> list;
		if (actionType == null || actionType.isBlank()) {
			list = pointSourceTypeService.getAll();
		} else {
			ActionType at = ActionType.valueOf(actionType);
			list = pointSourceTypeService.getAllByActionType(at);
		}
		return ResponseEntity.ok(JsendResponse.success(list));
	}

	@Operation(summary = "포인트 발생 유형 삭제", description = "포인트 발생 유형을 삭제합니다.")
	@DeleteMapping("/{point-source-type-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> deleteSource(
		@PathVariable(name = "point-source-type-id") Long pointSourceTypeId
	) {
		pointSourceTypeService.delete(pointSourceTypeId);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
