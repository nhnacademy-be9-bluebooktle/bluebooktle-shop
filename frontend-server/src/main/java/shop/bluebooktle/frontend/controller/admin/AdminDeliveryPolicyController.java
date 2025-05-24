package shop.bluebooktle.frontend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleUpdateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.frontend.service.AdminDeliveryRuleService;

@Controller
@RequestMapping("/admin/delivery/settings")
@RequiredArgsConstructor
public class AdminDeliveryPolicyController {

	private final AdminDeliveryRuleService service;

	@GetMapping
	public ModelAndView list(@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		PaginationData<DeliveryRuleResponse> data = service.getAllRules(page, size);
		ModelAndView mav = new ModelAndView("admin/delivery/delivery_rule_list");
		mav.addObject("deliveryRules", data);
		mav.addObject("regions", Region.values());
		return mav;
	}

	@GetMapping("/new")
	public ModelAndView showCreate() {
		ModelAndView mav = new ModelAndView("admin/delivery/delivery_rule_form");
		mav.addObject("form", new DeliveryRuleCreateRequest("", null, null, Region.ALL, true));
		mav.addObject("regions", Region.values());
		return mav;
	}

	@GetMapping("/{id}/edit")
	public ModelAndView showEdit(@PathVariable Long id) {
		DeliveryRuleResponse dto = service.getRuleById(id);
		ModelAndView mav = new ModelAndView("admin/delivery/delivery_rule_form");
		mav.addObject("form", new DeliveryRuleUpdateRequest(
			dto.deliveryFee(), dto.freeDeliveryThreshold(), dto.isActive()
		));
		mav.addObject("ruleId", id);
		mav.addObject("regions", Region.values());
		return mav;
	}

	@PostMapping
	public ModelAndView create(
		@ModelAttribute("form") @Valid DeliveryRuleCreateRequest form,
		BindingResult br,
		RedirectAttributes ra) {
		if (br.hasErrors()) {
			ModelAndView mav = new ModelAndView("admin/delivery/delivery_rule_form");
			mav.addObject("form", form);
			mav.addObject("regions", Region.values());
			return mav;
		}
		Long id = service.createRule(form);
		ra.addFlashAttribute("message", "배송 정책(#" + id + ")이 등록되었습니다.");
		return new ModelAndView("redirect:/admin/delivery/settings");
	}

	@PostMapping("/{id}")
	public ModelAndView update(
		@PathVariable Long id,
		@ModelAttribute("form") @Valid DeliveryRuleUpdateRequest form,
		BindingResult br,
		RedirectAttributes ra) {
		if (br.hasErrors()) {
			ModelAndView mav = new ModelAndView("admin/delivery/delivery_rule_form");
			mav.addObject("form", form);
			mav.addObject("ruleId", id);
			mav.addObject("regions", Region.values());
			return mav;
		}
		service.updateRule(id, form);
		ra.addFlashAttribute("message", "배송 정책(#" + id + ")이 수정되었습니다.");
		return new ModelAndView("redirect:/admin/delivery/settings");
	}

	@PostMapping("/{id}/delete")
	public ModelAndView delete(@PathVariable Long id, RedirectAttributes ra) {
		service.deleteRule(id);
		ra.addFlashAttribute("message", "배송 정책(#" + id + ")이 삭제되었습니다.");
		return new ModelAndView("redirect:/admin/delivery/settings");
	}
}
