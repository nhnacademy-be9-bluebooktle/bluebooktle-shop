package shop.bluebooktle.frontend.controller.admin;

import java.util.ArrayList;
import java.util.Arrays;
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

@Slf4j
@Controller
@RequestMapping("/admin/points/settings")
@RequiredArgsConstructor
public class AdminPointPolicyController {

	// private final AdminPointPolicyService adminPointPolicyService; // 실제 서비스 주입

	// --- DTO 정의 ---
	@Getter
	@Setter
	@ToString
	static class PointEarningRuleDto {
		private String sourceTypeKey; // 예: "SIGN_UP", "REVIEW_WRITE", "BOOK_PURCHASE"
		private String sourceTypeName; // 예: "회원가입 시", "리뷰 작성 시"
		private String earningType = "AMOUNT"; // "AMOUNT", "PERCENTAGE"
		private Double earningValue = 0.0;
		private boolean isActive = true;

		public PointEarningRuleDto() {
		}

		public PointEarningRuleDto(String sourceTypeKey, String sourceTypeName, String earningType, Double earningValue,
			boolean isActive) {
			this.sourceTypeKey = sourceTypeKey;
			this.sourceTypeName = sourceTypeName;
			this.earningType = earningType;
			this.earningValue = earningValue;
			this.isActive = isActive;
		}
	}

	@Getter
	@Setter
	@ToString
	static class MembershipPointRateDto {
		private Long membershipLevelId;
		private String membershipLevelName; // 예: "BRONZE", "SILVER"
		private Double additionalPurchaseRate = 0.0; // 추가 적립률 (%)
		private boolean isActive = true;

		public MembershipPointRateDto() {
		}

		public MembershipPointRateDto(Long membershipLevelId, String membershipLevelName, Double additionalPurchaseRate,
			boolean isActive) {
			this.membershipLevelId = membershipLevelId;
			this.membershipLevelName = membershipLevelName;
			this.additionalPurchaseRate = additionalPurchaseRate;
			this.isActive = isActive;
		}
	}

	@Getter
	@Setter
	@ToString
	static class PointPolicySettingsDto {
		private List<PointEarningRuleDto> earningRules = new ArrayList<>();
		private List<MembershipPointRateDto> membershipRates = new ArrayList<>();
	}

	@GetMapping
	public String showPointPolicySettings(Model model, HttpServletRequest request) {
		log.info("어드민 포인트 정책 설정 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("pageTitle", "포인트 정책 설정");
		model.addAttribute("currentURI", request.getRequestURI());

		// TODO: adminPointPolicyService.getPointPolicySettings() 호출
		// 임시 데이터
		PointPolicySettingsDto settingsDto = new PointPolicySettingsDto();
		settingsDto.getEarningRules().addAll(Arrays.asList(
			new PointEarningRuleDto("SIGN_UP", "회원가입 시", "AMOUNT", 1000.0, true),
			new PointEarningRuleDto("REVIEW_WRITE", "리뷰 작성 시", "AMOUNT", 500.0, true),
			new PointEarningRuleDto("BOOK_PURCHASE", "도서 구매 시", "PERCENTAGE", 1.0, true) // 구매 금액의 1%
		));
		settingsDto.getMembershipRates().addAll(Arrays.asList(
			new MembershipPointRateDto(1L, "BRONZE", 0.0, false), // 브론즈는 추가 적립 없음 (예시)
			new MembershipPointRateDto(2L, "SILVER", 0.5, true),  // 실버는 구매 시 0.5% 추가 적립
			new MembershipPointRateDto(3L, "GOLD", 1.0, true)     // 골드는 구매 시 1% 추가 적립
		));
		model.addAttribute("pointSettings", settingsDto);

		// 폼 내 선택 옵션 (필요시)
		model.addAttribute("earningTypeOptions", Arrays.asList(
			new AdminCouponController.SelectOption("AMOUNT", "정액(P)"),
			new AdminCouponController.SelectOption("PERCENTAGE", "정률(%)")
		));

		return "admin/point/point_settings_form";
	}

	@PostMapping
	public String savePointPolicySettings(//@Valid
		@ModelAttribute("pointSettings") PointPolicySettingsDto settingsDto,
		// BindingResult bindingResult, // 유효성 검사 시
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("포인트 정책 저장 요청: {}", settingsDto);

		// TODO: 유효성 검사 (예: 적립률/적립액이 음수가 아닌지 등)
		// if (bindingResult.hasErrors()) {
		//     log.warn("포인트 정책 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
		//     redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.pointSettings", bindingResult);
		//     redirectAttributes.addFlashAttribute("pointSettings", settingsDto); // 입력값 유지
		//     redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
		//     return "redirect:/admin/points/settings"; // 폼으로 다시 리다이렉트
		// }

		try {
			// TODO: 실제 서비스 로직 호출 (adminPointPolicyService.savePointPolicySettings(settingsDto))
			log.info("포인트 정책 저장 성공 (임시)");
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "포인트 정책이 성공적으로 저장되었습니다.");
		} catch (Exception e) {
			log.error("포인트 정책 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "포인트 정책 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("pointSettings", settingsDto); // 오류 시에도 입력값 유지
		}
		return "redirect:/admin/points/settings"; // 현재 페이지로 다시 리다이렉트하여 변경된 내용 확인
	}
}