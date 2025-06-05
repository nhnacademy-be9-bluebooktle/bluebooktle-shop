package shop.bluebooktle.backend.point.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.point.service.PointPolicyService;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points/rules")
@Tag(name = "포인트 규정 API", description = "포인트 정책 설정 API")
public class PointRuleController {

	private final PointPolicyService pointPolicyService;

	@GetMapping
	public ResponseEntity<JsendResponse<List<PointRuleResponse>>> getAllPointRules() {
		log.info("포인트 규정 목록 조회 요청");
		return ResponseEntity.ok(JsendResponse.success(pointPolicyService.getAll()));
	}

	@GetMapping("/type")
	public ResponseEntity<JsendResponse<PointRuleResponse>> getRuleBySourceTypeEnum(
		@RequestParam PointSourceTypeEnum pointSourceTypeEnum) {
		return ResponseEntity.ok(
			JsendResponse.success(pointPolicyService.getRuleBySourceTypeEnum(pointSourceTypeEnum)));
	}

}
