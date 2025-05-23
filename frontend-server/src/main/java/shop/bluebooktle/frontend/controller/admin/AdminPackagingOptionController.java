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
@RequestMapping("/admin/packaging/options")
@RequiredArgsConstructor
public class AdminPackagingOptionController {

	// private final AdminPackagingOptionService adminPackagingOptionService; // 실제 서비스

	// --- DTO 정의 ---
	@Getter
	@Setter
	@ToString
	static class PackagingOptionDto {
		private Long id;
		// @NotBlank
		private String name; // 포장명
		// @NotNull @PositiveOrZero
		private Double price; // 가격
		private String description; // 설명
		private Boolean isActive = true; // 활성화 여부
		private String imageUrl; // 이미지 URL

		public PackagingOptionDto() {
		}

		public PackagingOptionDto(Long id, String name, Double price, String description, Boolean isActive,
			String imageUrl) {
			this.id = id;
			this.name = name;
			this.price = price;
			this.description = description;
			this.isActive = isActive;
			this.imageUrl = imageUrl;
		}
	}

	// 임시 데이터 저장소
	private static final List<PackagingOptionDto> packagingOptionsStore = new ArrayList<>(Arrays.asList(
		new PackagingOptionDto(1L, "기본 선물 포장 (레드)", 2000.0, "빨간색 포장지와 리본으로 포장합니다.", true,
			"/images/packaging/red_gift.jpg"),
		new PackagingOptionDto(2L, "프리미엄 고급 포장 (골드)", 5000.0, "금색 보자기로 고급스럽게 포장합니다.", true,
			"/images/packaging/gold_premium.jpg"),
		new PackagingOptionDto(3L, "친환경 포장 (브라운)", 1500.0, "재활용 가능한 종이 포장재를 사용합니다.", false,
			"/images/packaging/eco_brown.jpg")
	));
	private static Long nextOptionId = 4L;

	@GetMapping
	public String listPackagingOptions(Model model, HttpServletRequest request) {
		log.info("어드민 포장 옵션 목록 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("pageTitle", "포장 옵션 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		// TODO: adminPackagingOptionService.getAllPackagingOptions() 호출
		model.addAttribute("packagingOptions", new ArrayList<>(packagingOptionsStore));

		return "admin/packaging/packaging_option_list";
	}

	@GetMapping({"/new", "/{optionId}/edit"})
	public String packagingOptionForm(@PathVariable(value = "optionId", required = false) Long optionId,
		Model model, HttpServletRequest request) {
		log.info("어드민 포장 옵션 폼 페이지 요청. URI: {}, optionId: {}", request.getRequestURI(), optionId);
		model.addAttribute("currentURI", request.getRequestURI());

		PackagingOptionDto optionDto;
		String pageTitle;

		if (optionId != null) {
			pageTitle = "포장 옵션 수정 (ID: " + optionId + ")";
			// TODO: adminPackagingOptionService.getPackagingOptionById(optionId) 호출
			optionDto = packagingOptionsStore.stream().filter(opt -> opt.getId().equals(optionId)).findFirst()
				.orElseGet(() -> {
					log.warn("요청한 포장 옵션 ID를 찾을 수 없음: {}", optionId);
					return new PackagingOptionDto(); // 또는 오류 처리
				});
		} else {
			pageTitle = "새 포장 옵션 추가";
			optionDto = new PackagingOptionDto();
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("packagingOption", optionDto);

		return "admin/packaging/packaging_option_form";
	}

	@PostMapping("/save")
	public String savePackagingOption(//@Valid
		@ModelAttribute("packagingOption") PackagingOptionDto optionDto,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("포장 옵션 저장 요청: {}", optionDto);

		// TODO: 유효성 검사 (이름 필수, 가격 음수 불가 등)
		if (optionDto.getName() == null || optionDto.getName().trim().isEmpty()) {
			bindingResult.rejectValue("name", "NotEmpty", "포장 옵션 이름은 필수입니다.");
		}
		if (optionDto.getPrice() == null || optionDto.getPrice() < 0) {
			bindingResult.rejectValue("price", "InvalidPrice", "가격은 0 이상이어야 합니다.");
		}

		if (bindingResult.hasErrors()) {
			log.warn("포장 옵션 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.packagingOption",
				bindingResult);
			redirectAttributes.addFlashAttribute("packagingOption", optionDto);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");

			if (optionDto.getId() != null) {
				return "redirect:/admin/packaging/options/" + optionDto.getId() + "/edit";
			} else {
				return "redirect:/admin/packaging/options/new";
			}
		}

		try {
			// TODO: 실제 서비스 로직 호출 (adminPackagingOptionService.savePackagingOption(optionDto))
			if (optionDto.getId() == null) { // 새 옵션
				optionDto.setId(nextOptionId++);
				packagingOptionsStore.add(optionDto);
			} else { // 기존 옵션 수정
				Optional<PackagingOptionDto> existingOpt = packagingOptionsStore.stream()
					.filter(opt -> opt.getId().equals(optionDto.getId())).findFirst();
				if (existingOpt.isPresent()) {
					PackagingOptionDto currentOpt = existingOpt.get();
					currentOpt.setName(optionDto.getName());
					currentOpt.setPrice(optionDto.getPrice());
					currentOpt.setDescription(optionDto.getDescription());
					currentOpt.setIsActive(optionDto.getIsActive());
					currentOpt.setImageUrl(optionDto.getImageUrl());
				} else {
					log.warn("수정하려는 포장 옵션 ID를 찾을 수 없음: {}", optionDto.getId());
					redirectAttributes.addFlashAttribute("globalErrorMessage", "포장 옵션을 찾을 수 없어 수정에 실패했습니다.");
					return "redirect:/admin/packaging/options";
				}
			}

			String action = (optionDto.getId() == null || packagingOptionsStore.stream()
				.anyMatch(opt -> opt.getId().equals(optionDto.getId()) && !opt.getName().equals(optionDto.getName()))) ?
				"등록" : "수정";
			log.info("포장 옵션 {} 성공 (임시): {}", action, optionDto.getName());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"포장 옵션 '" + optionDto.getName() + "' 정보가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("포장 옵션 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "포장 옵션 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("packagingOption", optionDto);
			if (optionDto.getId() != null) {
				return "redirect:/admin/packaging/options/" + optionDto.getId() + "/edit";
			} else {
				return "redirect:/admin/packaging/options/new";
			}
		}
		return "redirect:/admin/packaging/options";
	}

	@PostMapping("/{optionId}/delete")
	public String deletePackagingOption(@PathVariable Long optionId, RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("포장 옵션 삭제 요청: ID {}", optionId);
		try {
			// TODO: 실제 삭제 로직 (adminPackagingOptionService.deletePackagingOption(optionId))
			boolean removed = packagingOptionsStore.removeIf(opt -> opt.getId().equals(optionId));
			if (removed) {
				log.info("임시 포장 옵션 삭제 성공 처리: ID {}", optionId);
				redirectAttributes.addFlashAttribute("globalSuccessMessage",
					"포장 옵션(ID: " + optionId + ")이 성공적으로 삭제되었습니다.");
			} else {
				log.warn("삭제하려는 포장 옵션 ID를 찾을 수 없음: {}", optionId);
				redirectAttributes.addFlashAttribute("globalErrorMessage", "삭제하려는 포장 옵션을 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			log.error("포장 옵션 삭제 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "포장 옵션 삭제 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/packaging/options";
	}
}