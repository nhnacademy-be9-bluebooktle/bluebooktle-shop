package shop.bluebooktle.frontend.controller.admin;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.frontend.service.AdminPackagingOptionService;

@Slf4j
@Controller
@RequestMapping("/admin/packaging/options")
@RequiredArgsConstructor
public class AdminPackagingOptionController {

	private final AdminPackagingOptionService packagingOptionService;

	/** 포장 옵션 목록 조회 */
	@GetMapping
	public String listPackagingOptions(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
		Page<PackagingOptionInfoResponse> optionPage = packagingOptionService.getPackagingOptions(page, size,
			searchKeyword);
		log.info("어드민 포장 옵션 목록 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("pageTitle", "포장 옵션 관리");
		model.addAttribute("currentURI", request.getRequestURI());
		model.addAttribute("optionPackagings", optionPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", optionPage.getTotalPages());
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("size", size);

		return "admin/packaging/packaging_option_list";
	}

	/** 포장 옵션 등록 또는 수정 폼 진입 */
	@GetMapping({"/new", "/{optionId}/edit"})
	public String packagingOptionForm(@PathVariable(value = "optionId", required = false) Long optionId,
		Model model,
		HttpServletRequest request) {
		log.info("어드민 포장 옵션 폼 페이지 요청. URI: {}, optionId: {}", request.getRequestURI(), optionId);
		model.addAttribute("currentURI", request.getRequestURI());

		PackagingOptionInfoResponse option;
		String pageTitle;

		if (optionId != null) {
			pageTitle = "포장 옵션 수정 (ID: " + optionId + ")";
			option = packagingOptionService.getPackagingOption(optionId);
		} else {
			pageTitle = "새 포장 옵션 추가";
			option = PackagingOptionInfoResponse.builder()
				.id(null)
				.name("")
				.price(BigDecimal.ZERO)
				.build();
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("packagingOption", option);

		return "admin/packaging/packaging_option_form";
	}

	/** 포장 옵션 저장 */
	@PostMapping("/save")
	public String savePackagingOption(//@Valid
		@ModelAttribute("packagingOption")
		PackagingOptionInfoResponse option,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {
		log.info("포장 옵션 저장 요청: {}", option);

		if (option.getName() == null || option.getName().trim().isEmpty()) {
			bindingResult.rejectValue("name", "NotEmpty", "포장 옵션 이름은 필수입니다.");
		}

		if (bindingResult.hasErrors()) {
			log.warn("포장 옵션 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.packagingOption",
				bindingResult);
			redirectAttributes.addFlashAttribute("packagingOption", option);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");

			if (option.getId() != null) {
				return "redirect:/admin/packaging/options/" + option.getId() + "/edit";
			} else {
				return "redirect:/admin/packaging/options/new";
			}
		}

		try {
			if (option.getId() == null) { // 새 옵션
				packagingOptionService.createPackingOption(
					new PackagingOptionInfoResponse(option.getId(), option.getName(), option.getPrice()));
			} else { // 기존 옵션 수정
				packagingOptionService.updatePackingOption(option.getId(), option);
			}
			String action = (option.getId() == null) ? "등록" : "수정";
			log.info("포장 옵션 {} 처리 (임시): Name={}, DeletedAt={}", action, option.getName());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"포장 옵션 '" + option.getName() + "'가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("포장 옵션 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "포장 옵션 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("packagingOption", option);
			if (option.getId() != null) {
				return "redirect:/admin/packaging/options/" + option.getId() + "/edit";
			} else {
				return "redirect:/admin/packaging/options/new";
			}
		}
		return "redirect:/admin/packaging/options";
	}

	@PostMapping("/{optionId}/delete")
	public String deletePackagingOption(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		log.info("포장 옵션 삭제 요청: ID {}", id);
		try {
			packagingOptionService.deletePackingOption(id);
			log.info("임시 포장 옵션 삭제 성공 처리: ID {}", id);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"포장 옵션(ID: " + id + ")가 성공적으로 삭제되었습니다.");
		} catch (Exception e) {
			log.error("포장 옵션 삭제 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "포장 옵션 삭제 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/packaging/options";
	}
}
