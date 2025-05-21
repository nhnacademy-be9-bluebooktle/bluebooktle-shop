package shop.bluebooktle.backend.point.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.point.service.PointPolicyService;
import shop.bluebooktle.backend.point.service.PointSourceTypeService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points/rules")
@Tag(name = "포인트 규정 API", description = "포인트 정책 설정 API")
public class PointRuleController {

	private final PointPolicyService pointPolicyService;
	private final PointSourceTypeService pointSourceTypeService;

	@GetMapping
	public ResponseEntity<JsendResponse<List<PointRuleResponse>>> getAllPointRules() {
		return ResponseEntity.ok(JsendResponse.success(pointPolicyService.getAll()));
	}

}
