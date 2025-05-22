package shop.bluebooktle.frontend.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.frontend.service.PointService;

@Slf4j
@Controller
@RequestMapping("/admin/points/settings")
@RequiredArgsConstructor
public class AdminPointPolicyController {

	private final PointService pointService;

	@GetMapping
	public String showPointPolicySettings(Model model, HttpServletRequest request) {
		log.info("어드민 포인트 정책 설정 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("pageTitle", "포인트 정책 설정");
		model.addAttribute("currentURI", request.getRequestURI());

		// 실제 서비스에서 포인트 규정 목록 조회
		List<PointRuleResponse> earningRules = pointService.getAllRules();
		model.addAttribute("earningRules", earningRules);

		// TODO: membershipRates 설정
		model.addAttribute("membershipRates", new ArrayList<>());

		// 폼 내 선택 옵션
		model.addAttribute("earningTypeOptions", List.of(
			new AdminCouponController.SelectOption("AMOUNT", "정액(P)"),
			new AdminCouponController.SelectOption("PERCENTAGE", "정률(%)")
		));

		return "admin/point/point_settings_form";
	}

	@PostMapping
	public String savePointPolicySettings(
		@ModelAttribute("earningRules") List<PointRuleResponse> earningRules,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request
	) {
		log.info("포인트 정책 저장 요청: {}", earningRules);

		try {
			for (PointRuleResponse rule : earningRules) {
				if (rule.pointPolicyId() == null) {
					// 신규 생성
					pointService.createPolicy(
						new PointPolicyCreateRequest(
							rule.pointSourceTypeId(),
							rule.policyType(),
							rule.value()
						)
					);
				} else {
					// 업데이트
					pointService.updatePolicy(
						new PointPolicyUpdateRequest(
							rule.pointPolicyId(),
							rule.policyType(),
							rule.value(),
							rule.isActive()
						)
					);
				}
			}
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "포인트 정책이 성공적으로 저장되었습니다.");
		} catch (Exception e) {
			log.error("포인트 정책 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "포인트 정책 저장 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/points/settings";
	}

	// --- 뷰 모델 DTO ---
	@Getter
	@Setter
	@ToString
	static class PointPolicySettingsDto {
		private List<PointRuleResponse> earningRules = new ArrayList<>();
		private List<Object> membershipRates = new ArrayList<>();
	}
}
