package shop.bluebooktle.backend.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.admin.service.DashboardService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.admin.DashboardStatusResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "관리자 대시보드 조회", description = "관리자 대시보드에 표시할 데이터를 조회합니다.")
public class DashboardController {

	private final DashboardService dashboardService;

	@Operation(summary = "관리자 대시보드 데이터", description = "관리자 대시보드에 표시할 데이터를 조회합니다.")
	@GetMapping("/status")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<DashboardStatusResponse>> getDashboardStatus() {
		DashboardStatusResponse response = dashboardService.getDashboardStatus();
		return ResponseEntity.ok(JsendResponse.success(response));
	}
}