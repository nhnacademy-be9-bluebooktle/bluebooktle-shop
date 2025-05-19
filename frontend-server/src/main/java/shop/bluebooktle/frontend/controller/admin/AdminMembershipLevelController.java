package shop.bluebooktle.frontend.controller.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
@RequestMapping("/admin/membership/levels")
@RequiredArgsConstructor
public class AdminMembershipLevelController {

	// private final AdminMembershipLevelService adminMembershipLevelService;

	// --- DTO 정의 ---
	@Getter
	@Setter
	@ToString
	static class MembershipLevelDto {
		private Long id;
		// @NotBlank
		private String levelName; // 예: "BRONZE", "SILVER"
		// @NotNull @PositiveOrZero
		private Double minNetSpent; // 최소 순수 지출액 (등급 조건)
		private Double maxNetSpent; // 최대 순수 지출액 (다음 등급과의 경계, null 가능)
		private String description;
		// @NotNull @PositiveOrZero
		private Double pointRate; // 구매 시 추가 포인트 적립률 (%)
		private String iconUrl;
		private Boolean isActive = true;

		public MembershipLevelDto() {
		}

		public MembershipLevelDto(Long id, String levelName, Double minNetSpent, Double maxNetSpent, String description,
			Double pointRate, Boolean isActive) {
			this.id = id;
			this.levelName = levelName;
			this.minNetSpent = minNetSpent;
			this.maxNetSpent = maxNetSpent;
			this.description = description;
			this.pointRate = pointRate;
			this.isActive = isActive;
		}
	}

	// 임시 데이터 저장소
	private static final List<MembershipLevelDto> levelStore = new ArrayList<>(Arrays.asList(
		new MembershipLevelDto(1L, "BRONZE", 0.0, 99999.99, "기본 회원 등급", 0.5, true),
		new MembershipLevelDto(2L, "SILVER", 100000.0, 499999.99, "우수 회원 등급", 1.0, true),
		new MembershipLevelDto(3L, "GOLD", 500000.0, 1999999.99, "최우수 회원 등급", 1.5, true),
		new MembershipLevelDto(4L, "PLATINUM", 2000000.0, null, "VIP 회원 등급", 2.0, false)
	));
	private static Long nextLevelId = 5L;

	@GetMapping
	public String listMembershipLevels(Model model, HttpServletRequest request) {
		log.info("어드민 회원 등급 정책 목록 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("pageTitle", "회원 등급 정책 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		// TODO: adminMembershipLevelService.getAllLevels() 호출 (minNetSpent 순으로 정렬)
		List<MembershipLevelDto> sortedLevels = levelStore.stream()
			.sorted(Comparator.comparing(MembershipLevelDto::getMinNetSpent, Comparator.nullsLast(Double::compareTo)))
			.collect(Collectors.toList());
		model.addAttribute("membershipLevels", sortedLevels);

		return "admin/membership/level_list";
	}

	@GetMapping({"/new", "/{levelId}/edit"})
	public String membershipLevelForm(@PathVariable(value = "levelId", required = false) Long levelId,
		Model model, HttpServletRequest request) {
		log.info("어드민 회원 등급 정책 폼 페이지 요청. URI: {}, levelId: {}", request.getRequestURI(), levelId);
		model.addAttribute("currentURI", request.getRequestURI());

		MembershipLevelDto levelDto;
		String pageTitle;

		if (levelId != null) {
			pageTitle = "회원 등급 수정 (ID: " + levelId + ")";
			// TODO: adminMembershipLevelService.getLevelById(levelId) 호출
			levelDto = levelStore.stream().filter(level -> level.getId().equals(levelId)).findFirst()
				.orElseGet(() -> {
					log.warn("요청한 회원 등급 ID를 찾을 수 없음: {}", levelId);
					return new MembershipLevelDto();
				});
		} else {
			pageTitle = "새 회원 등급 추가";
			levelDto = new MembershipLevelDto();
			// 새 등급 추가 시, minNetSpent는 이전 최고 등급의 maxNetSpent + 0.01 또는 관리자가 입력하도록 유도
			Optional<MembershipLevelDto> highestLevel = levelStore.stream()
				.filter(l -> l.getMaxNetSpent() != null) // max가 null이 아닌것 중 가장 큰 값
				.max(Comparator.comparing(MembershipLevelDto::getMaxNetSpent));
			if (highestLevel.isPresent() && highestLevel.get().getMaxNetSpent() != null) {
				// levelDto.setMinNetSpent(highestLevel.get().getMaxNetSpent() + 0.01); // 예시 기본값
			} else {
				// levelDto.setMinNetSpent(0.0); // 첫 등급일 경우
			}
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("membershipLevel", levelDto);

		return "admin/membership/level_form";
	}

	@PostMapping("/save")
	public String saveMembershipLevel(//@Valid
		@ModelAttribute("membershipLevel") MembershipLevelDto levelDto,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("회원 등급 정책 저장 요청: {}", levelDto);

		// TODO: 유효성 검사 (등급명 필수, 금액 음수 불가, min < max 논리 검사 등)
		if (levelDto.getLevelName() == null || levelDto.getLevelName().trim().isEmpty()) {
			bindingResult.rejectValue("levelName", "NotEmpty", "등급 이름은 필수입니다.");
		}
		if (levelDto.getMinNetSpent() == null || levelDto.getMinNetSpent() < 0) {
			bindingResult.rejectValue("minNetSpent", "InvalidAmount", "최소 지출액은 0 이상이어야 합니다.");
		}
		if (levelDto.getMaxNetSpent() != null && levelDto.getMaxNetSpent() < 0) {
			bindingResult.rejectValue("maxNetSpent", "InvalidAmount", "최대 지출액은 0 이상이어야 합니다.");
		}
		if (levelDto.getMaxNetSpent() != null && levelDto.getMinNetSpent() != null
			&& levelDto.getMaxNetSpent() <= levelDto.getMinNetSpent()) {
			bindingResult.rejectValue("maxNetSpent", "MinMaxOrder", "최대 지출액은 최소 지출액보다 커야 합니다 (또는 비워두세요).");
		}
		if (levelDto.getPointRate() == null || levelDto.getPointRate() < 0) {
			bindingResult.rejectValue("pointRate", "InvalidRate", "포인트 적립률은 0 이상이어야 합니다.");
		}
		// TODO: 다른 등급과의 min/max 금액 구간 중첩 검사 필요

		if (bindingResult.hasErrors()) {
			log.warn("회원 등급 정책 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.membershipLevel",
				bindingResult);
			redirectAttributes.addFlashAttribute("membershipLevel", levelDto);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");

			if (levelDto.getId() != null) {
				return "redirect:/admin/membership/levels/" + levelDto.getId() + "/edit";
			} else {
				return "redirect:/admin/membership/levels/new";
			}
		}

		try {
			// TODO: 실제 서비스 로직 호출 (adminMembershipLevelService.saveLevel(levelDto))
			if (levelDto.getId() == null) {
				levelDto.setId(nextLevelId++);
				levelStore.add(levelDto);
			} else {
				Optional<MembershipLevelDto> existingLevelOpt = levelStore.stream()
					.filter(l -> l.getId().equals(levelDto.getId())).findFirst();
				if (existingLevelOpt.isPresent()) {
					MembershipLevelDto currentLevel = existingLevelOpt.get();
					currentLevel.setLevelName(levelDto.getLevelName());
					currentLevel.setMinNetSpent(levelDto.getMinNetSpent());
					currentLevel.setMaxNetSpent(levelDto.getMaxNetSpent());
					currentLevel.setDescription(levelDto.getDescription());
					currentLevel.setPointRate(levelDto.getPointRate());
					currentLevel.setIconUrl(levelDto.getIconUrl());
					currentLevel.setIsActive(levelDto.getIsActive());
				} else {
					log.warn("수정하려는 회원 등급 ID를 찾을 수 없음: {}", levelDto.getId());
					redirectAttributes.addFlashAttribute("globalErrorMessage", "회원 등급을 찾을 수 없어 수정에 실패했습니다.");
					return "redirect:/admin/membership/levels";
				}
			}
			String action = "저장"; // 등록/수정 구분은 실제 DB 작업 후 판단
			log.info("회원 등급 정책 {} 성공 (임시): {}", action, levelDto.getLevelName());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"회원 등급 '" + levelDto.getLevelName() + "' 정보가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("회원 등급 정책 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "회원 등급 정책 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("membershipLevel", levelDto);
			if (levelDto.getId() != null) {
				return "redirect:/admin/membership/levels/" + levelDto.getId() + "/edit";
			} else {
				return "redirect:/admin/membership/levels/new";
			}
		}
		return "redirect:/admin/membership/levels";
	}

	@PostMapping("/{levelId}/delete")
	public String deleteMembershipLevel(@PathVariable Long levelId, RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("회원 등급 정책 삭제 요청: ID {}", levelId);
		// TODO: 해당 등급에 속한 회원이 있는지 확인하는 로직 필요. 있다면 삭제 불가 또는 다른 등급으로 이전 정책 필요.
		// 여기서는 단순히 리스트에서 제거하는 것으로 가정.
		try {
			boolean removed = levelStore.removeIf(level -> level.getId().equals(levelId));
			if (removed) {
				log.info("임시 회원 등급 정책 삭제 성공 처리: ID {}", levelId);
				redirectAttributes.addFlashAttribute("globalSuccessMessage",
					"회원 등급(ID: " + levelId + ")이 성공적으로 삭제되었습니다.");
			} else {
				log.warn("삭제하려는 회원 등급 ID를 찾을 수 없음: {}", levelId);
				redirectAttributes.addFlashAttribute("globalErrorMessage", "삭제하려는 회원 등급을 찾을 수 없습니다.");
			}
		} catch (Exception e) { // 예: 사용 중인 등급 삭제 시 발생하는 예외 등
			log.error("회원 등급 정책 삭제 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "회원 등급 정책 삭제 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/membership/levels";
	}
}