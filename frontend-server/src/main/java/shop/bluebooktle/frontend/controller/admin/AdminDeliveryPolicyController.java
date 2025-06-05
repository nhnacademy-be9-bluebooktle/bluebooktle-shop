package shop.bluebooktle.frontend.controller.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

@Slf4j
@Controller
@RequestMapping("/admin/delivery/settings") // 또는 /admin/delivery/rules
@RequiredArgsConstructor
public class AdminDeliveryPolicyController {

	// private final AdminDeliveryPolicyService adminDeliveryPolicyService;

	// --- DTO 정의 ---
	@Getter
	@Setter
	@ToString
	static class DeliveryRuleDto {
		private Long id;
		private String ruleName; // 예: "기본 배송비", "5만원 이상 무료"
		private Double baseShippingFee; // 기본 배송비 (이 규칙이 배송비 부과 규칙일 때)
		private Double freeShippingThreshold; // 무료 배송 조건 금액 (이 규칙이 무료배송 규칙일 때)
		private String regionType = "ALL"; // "ALL", "JEJU_ISLAND", "MOUNTAINOUS_AREAS" 등
		private String description;
		private Boolean isActive = true;
		private Integer priority = 10; // 규칙 적용 우선순위 (낮을수록 우선)

		public DeliveryRuleDto() {
		}

		public DeliveryRuleDto(Long id, String ruleName, Double baseShippingFee, Double freeShippingThreshold,
			String regionType, Boolean isActive, Integer priority) {
			this.id = id;
			this.ruleName = ruleName;
			this.baseShippingFee = baseShippingFee;
			this.freeShippingThreshold = freeShippingThreshold;
			this.regionType = regionType;
			this.isActive = isActive;
			this.priority = priority;
		}
	}

	// 임시 데이터 저장소
	private static final List<DeliveryRuleDto> deliveryRulesStore = new ArrayList<>(Arrays.asList(
		new DeliveryRuleDto(1L, "기본 배송비", 3000.0, null, "ALL", true, 10),
		new DeliveryRuleDto(2L, "5만원 이상 구매 시 무료배송", 0.0, 50000.0, "ALL", true, 5),
		new DeliveryRuleDto(3L, "제주도 추가 배송비", 3000.0, null, "JEJU_ISLAND", true, 20),
		new DeliveryRuleDto(4L, "도서산간 추가 배송비", 5000.0, null, "MOUNTAINOUS_AREAS", false, 25)
	));
	private static Long nextId = 5L;

	@GetMapping
	public String listDeliveryRules(Model model, HttpServletRequest request) {
		log.info("어드민 배송 정책 목록 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("pageTitle", "배송 정책 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		// TODO: adminDeliveryPolicyService.getAllDeliveryRules() 호출
		model.addAttribute("deliveryRules", new ArrayList<>(deliveryRulesStore)); // 수정 가능하도록 새 리스트 전달

		return "admin/delivery/delivery_rule_list";
	}

	@GetMapping({"/new", "/{ruleId}/edit"})
	public String deliveryRuleForm(@PathVariable(value = "ruleId", required = false) Long ruleId,
		Model model, HttpServletRequest request) {
		log.info("어드민 배송 정책 폼 페이지 요청. URI: {}, ruleId: {}", request.getRequestURI(), ruleId);
		model.addAttribute("currentURI", request.getRequestURI());

		DeliveryRuleDto ruleDto;
		String pageTitle;

		if (ruleId != null) {
			pageTitle = "배송 정책 수정 (ID: " + ruleId + ")";
			// TODO: adminDeliveryPolicyService.getDeliveryRuleById(ruleId) 호출
			ruleDto = deliveryRulesStore.stream().filter(r -> r.getId().equals(ruleId)).findFirst()
				.orElseGet(() -> {
					log.warn("요청한 배송 규칙 ID를 찾을 수 없음: {}", ruleId);
					return new DeliveryRuleDto(); // 빈 객체 또는 오류 처리
				});
		} else {
			pageTitle = "새 배송 정책 추가";
			ruleDto = new DeliveryRuleDto();
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("deliveryRule", ruleDto);

		// 적용 지역 타입 옵션 (실제로는 Enum 또는 DB에서 관리)
		// model.addAttribute("regionTypes", Arrays.asList(
		// 	new AdminCouponController.SelectOption("ALL", "전국"),
		// 	new AdminCouponController.SelectOption("JEJU_ISLAND", "제주도"),
		// 	new AdminCouponController.SelectOption("MOUNTAINOUS_AREAS", "도서산간지역"),
		// 	new AdminCouponController.SelectOption("EXCEPT_JEJU_MOUNTAINOUS", "제주/도서산간 제외")
		// ));

		return "admin/delivery/delivery_rule_form";
	}

	@PostMapping("/save")
	public String saveDeliveryRule(//@Valid
		@ModelAttribute("deliveryRule") DeliveryRuleDto ruleDto,
		BindingResult bindingResult, // 유효성 검사 결과
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("배송 정책 저장 요청: {}", ruleDto);

		// TODO: 유효성 검사 (ruleName 필수, 금액 음수 불가 등)
		if (ruleDto.getRuleName() == null || ruleDto.getRuleName().trim().isEmpty()) {
			bindingResult.rejectValue("ruleName", "NotEmpty", "규칙 이름은 필수입니다.");
		}
		// 무료 배송 임계값이 있으면서 배송비도 있으면 혼란 -> 로직 검토 필요
		// 기본 배송비와 무료 배송 임계값 중 하나만 주로 사용됨, 또는 타입으로 구분

		if (bindingResult.hasErrors()) {
			log.warn("배송 정책 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.deliveryRule",
				bindingResult);
			redirectAttributes.addFlashAttribute("deliveryRule", ruleDto); // 입력값 유지
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");

			if (ruleDto.getId() != null) {
				return "redirect:/admin/delivery/settings/" + ruleDto.getId() + "/edit";
			} else {
				return "redirect:/admin/delivery/settings/new";
			}
		}

		try {
			// TODO: 실제 서비스 로직 호출 (adminDeliveryPolicyService.saveDeliveryRule(ruleDto))
			if (ruleDto.getId() == null) { // 새 규칙
				ruleDto.setId(nextId++);
				deliveryRulesStore.add(ruleDto);
			} else { // 기존 규칙 수정
				Optional<DeliveryRuleDto> existingRuleOpt = deliveryRulesStore.stream()
					.filter(r -> r.getId().equals(ruleDto.getId())).findFirst();
				if (existingRuleOpt.isPresent()) {
					DeliveryRuleDto existingRule = existingRuleOpt.get();
					existingRule.setRuleName(ruleDto.getRuleName());
					existingRule.setBaseShippingFee(ruleDto.getBaseShippingFee());
					existingRule.setFreeShippingThreshold(ruleDto.getFreeShippingThreshold());
					existingRule.setRegionType(ruleDto.getRegionType());
					existingRule.setDescription(ruleDto.getDescription());
					existingRule.setIsActive(ruleDto.getIsActive());
					existingRule.setPriority(ruleDto.getPriority());
				} else {
					// 오류 처리: 수정하려는 ID가 없음
					log.warn("수정하려는 배송 규칙 ID를 찾을 수 없음: {}", ruleDto.getId());
					redirectAttributes.addFlashAttribute("globalErrorMessage", "배송 규칙을 찾을 수 없어 수정에 실패했습니다.");
					return "redirect:/admin/delivery/settings";
				}
			}

			String action = (ruleDto.getId() == null || deliveryRulesStore.stream()
				.noneMatch(r -> r.getId().equals(ruleDto.getId()) && r.getRuleName().equals(ruleDto.getRuleName()))) ?
				"등록" : "수정"; // 임시 id 할당 후 로직
			log.info("배송 정책 {} 성공 (임시): {}", action, ruleDto.getRuleName());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"배송 정책 '" + ruleDto.getRuleName() + "' 정보가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("배송 정책 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "배송 정책 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("deliveryRule", ruleDto);
			if (ruleDto.getId() != null) {
				return "redirect:/admin/delivery/settings/" + ruleDto.getId() + "/edit";
			} else {
				return "redirect:/admin/delivery/settings/new";
			}
		}
		return "redirect:/admin/delivery/settings";
	}

	@PostMapping("/{ruleId}/delete")
	public String deleteDeliveryRule(@PathVariable Long ruleId, RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("배송 정책 삭제 요청: ID {}", ruleId);
		try {
			// TODO: 실제 삭제 로직 (adminDeliveryPolicyService.deleteDeliveryRule(ruleId))
			boolean removed = deliveryRulesStore.removeIf(rule -> rule.getId().equals(ruleId));
			if (removed) {
				log.info("임시 배송 정책 삭제 성공 처리: ID {}", ruleId);
				redirectAttributes.addFlashAttribute("globalSuccessMessage",
					"배송 정책(ID: " + ruleId + ")이 성공적으로 삭제되었습니다.");
			} else {
				log.warn("삭제하려는 배송 규칙 ID를 찾을 수 없음: {}", ruleId);
				redirectAttributes.addFlashAttribute("globalErrorMessage", "삭제하려는 배송 정책을 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			log.error("배송 정책 삭제 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "배송 정책 삭제 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/delivery/settings";
	}
}