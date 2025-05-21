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
import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointSourceTypeResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points/source")
@Tag(name = "포인트 발생 유형 API", description = "포인트 발생 유형 추가, 삭제 관련 API")
public class PointSourceTypeController {

	private final PointSourceTypeService pointSourceTypeService;

	@Operation(summary = "포인트 발생 유형 생성", description = "포인트 발생 유형을 생성합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> createSource(
		@Valid @RequestBody PointSourceTypeCreateRequest request) {
		pointSourceTypeService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "단건 조회", description = "포인트 발생 유형을 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<JsendResponse<PointSourceTypeResponse>> getSource(@PathVariable Long id) {
		PointSourceTypeResponse resp = pointSourceTypeService.get(id);
		return ResponseEntity.ok(JsendResponse.success(resp));
	}

	@Operation(summary = "포인트 발생 유형 조회", description = " 전체 또는 특정 ActionType별 조회합니다.")
	@GetMapping
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
	@DeleteMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> deleteSource(@PathVariable Long id) {
		pointSourceTypeService.delete(id);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
