package shop.bluebooktle.frontend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleUpdateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.frontend.service.AdminDeliveryRuleService;

@Slf4j
@Controller
@RequestMapping("/admin/delivery/settings")
@RequiredArgsConstructor
public class AdminDeliveryRuleController {

	private final AdminDeliveryRuleService service;

	@GetMapping
	public ModelAndView list(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		HttpServletRequest request
	) {
		PaginationData<DeliveryRuleResponse> data = service.getAllRules(page, size);
		ModelAndView mav = new ModelAndView("admin/delivery/delivery_rule_list");
		mav.addObject("pageTitle", "배송 정책 설정");
		mav.addObject("currentURI", request.getRequestURI());
		mav.addObject("deliveryRules", data);
		mav.addObject("regions", Region.values());
		return mav;
	}

	@GetMapping("/new")
	public ModelAndView showCreate(HttpServletRequest request,
		@ModelAttribute DeliveryRuleCreateRequest deliveryRuleCreateRequest,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.deliveryRule",
				bindingResult);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
		}
		ModelAndView mav = new ModelAndView("admin/delivery/delivery_rule_form");
		mav.addObject("pageTitle", "새 배송 정책 추가");
		mav.addObject("currentURI", request.getRequestURI());
		mav.addObject("form", new DeliveryRuleCreateRequest("", null, null, Region.ALL, true));
		mav.addObject("regions", Region.values());
		return mav;
	}

	@PostMapping
	public ModelAndView create(
		@ModelAttribute("form") @Valid DeliveryRuleCreateRequest form,
		BindingResult br,
		RedirectAttributes ra,
		HttpServletRequest request
	) {
		if (br.hasErrors()) {
			ModelAndView mav = new ModelAndView("admin/delivery/delivery_rule_form");
			mav.addObject("pageTitle", "새 배송 정책 추가");
			mav.addObject("currentURI", request.getRequestURI());
			mav.addObject("form", form);
			mav.addObject("regions", Region.values());
			return mav;
		}
		Long id = service.createRule(form);
		ra.addFlashAttribute("message", "배송 정책(#" + id + ")이 등록되었습니다.");
		return new ModelAndView("redirect:/admin/delivery/settings");
	}

	@PostMapping("/{id}")
	public String update(
		@PathVariable Long id,
		@ModelAttribute("form") @Valid DeliveryRuleUpdateRequest form,
		BindingResult br,
		RedirectAttributes ra,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		// 유효성 에러가 있으면 플래시에 바인딩결과와 폼 데이터를 넣고 목록으로 되돌림
		if (br.hasErrors()) {
			ra.addFlashAttribute("org.springframework.validation.BindingResult.form", br);
			ra.addFlashAttribute("form", form);
			return "redirect:/admin/delivery/settings?page=" + page + "&size=" + size;
		}

		// 업데이트 수행
		service.updateRule(id, form);
		ra.addFlashAttribute("message", "배송 정책(#" + id + ")이 수정되었습니다.");

		// 페이지 정보 유지하며 목록으로 리다이렉트
		return "redirect:/admin/delivery/settings?page=" + page + "&size=" + size;
	}

	@DeleteMapping("/{id}")
	public ModelAndView delete(
		@PathVariable Long id,
		RedirectAttributes ra
	) {
		service.deleteRule(id);
		ra.addFlashAttribute("message", "배송 정책(#" + id + ")이 삭제되었습니다.");
		return new ModelAndView("redirect:/admin/delivery/settings");
	}
}
