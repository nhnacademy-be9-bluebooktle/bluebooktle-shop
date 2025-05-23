package shop.bluebooktle.frontend.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.point.PolicyType;
import shop.bluebooktle.common.dto.point.request.PointPolicyCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyUpdateRequest;
import shop.bluebooktle.common.dto.point.request.PointPolicyWithSourceCreateRequest;
import shop.bluebooktle.common.dto.point.request.PointSourceTypeCreateRequest;
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
		// model.addAttribute("earningTypeOptions", List.of(
		// 	new AdminCouponController.SelectOption("AMOUNT", "정액(P)"),
		// 	new AdminCouponController.SelectOption("PERCENTAGE", "정률(%)")
		// ));

		return "admin/point/point_settings_form";
	}

	@PostMapping("/{policyId}")
	public String updateSinglePolicy(
		@PathVariable Long policyId,
		PointPolicyUpdateRequest req,
		RedirectAttributes ra
	) {
		try {
			// URL 에서 넘어온 policyId 확인(보안검증 생략)
			req = new PointPolicyUpdateRequest(
				policyId,
				req.policyType(),
				req.value(),
				req.isActive()
			);
			pointService.updatePolicy(req);
			ra.addFlashAttribute("globalSuccessMessage",
				"Policy " + policyId + "가 수정되었습니다.");
		} catch (Exception e) {
			ra.addFlashAttribute("globalErrorMessage",
				"Policy " + policyId + " 수정 실패: " + e.getMessage());
		}
		return "redirect:/admin/points/settings";
	}

	@PostMapping("/new")
	public String createNewPointPolicy(
		@ModelAttribute PointPolicyWithSourceCreateRequest req,
		RedirectAttributes redirectAttributes
	) {
		try {
			Long newSourceTypeId = pointService.createSourceType(
				new PointSourceTypeCreateRequest(req.actionType(), req.sourceType())
			);

			PointPolicyCreateRequest policyReq =
				new PointPolicyCreateRequest(newSourceTypeId, req.policyType(), req.value());
			Long newPolicyId = pointService.createPolicy(policyReq);

			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"생성 완료: PolicyID=" + newPolicyId + " / SourceTypeID=" + newSourceTypeId);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "생성 실패: " + e.getMessage());
		}
		return "redirect:/admin/points/settings";
	}

	@GetMapping("/new")
	public String showNewPolicyForm(Model model) {
		// 1) 폼 바인딩용 DTO (actionType, sourceType, policyType, value)
		model.addAttribute("policyCreateRequest",
			new PointPolicyWithSourceCreateRequest(null, null, null, null));
		// 2) 정책 유형 드롭다운 옵션
		model.addAttribute("policyTypes", PolicyType.values());
		return "admin/point/policy_form";
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
