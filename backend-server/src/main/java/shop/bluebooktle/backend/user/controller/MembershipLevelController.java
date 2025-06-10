package shop.bluebooktle.backend.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.service.MembershipLevelService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;

@Slf4j
@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@Tag(name = "회원 등급 API", description = "모든 회원 등급 정보 API")
public class MembershipLevelController {

	private final MembershipLevelService membershipLevelService;

	@GetMapping
	@Operation(summary = "모든 회원 등급 조회", description = "모든 회원 등급 정보를 조회합니다.")
	public ResponseEntity<JsendResponse<List<MembershipLevelDetailDto>>> getMembershipLevels(
	) {
		List<MembershipLevelDetailDto> dto = membershipLevelService.getAllMembershipLevels();
		return ResponseEntity.ok(JsendResponse.success(dto));
	}
}
