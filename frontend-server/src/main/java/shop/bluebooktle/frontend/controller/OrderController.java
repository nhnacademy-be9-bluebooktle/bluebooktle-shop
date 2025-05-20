package shop.bluebooktle.frontend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import shop.bluebooktle.common.dto.payment.request.TossConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.TossConfirmResponse;
import shop.bluebooktle.frontend.service.TossPaymentsService;

@Controller
@RequestMapping("/order")
public class OrderController {

	private final TossPaymentsService tossPaymentsService;
	@Value("${toss.client-key}")
	private String clientKey;

	public OrderController(TossPaymentsService tossPaymentsService) {
		this.tossPaymentsService = tossPaymentsService;
	}

	@GetMapping("/checkout")
	public ModelAndView checkoutPage() {
		ModelAndView mav = new ModelAndView("order/checkout");
		mav.addObject("clientKey", clientKey);
		mav.addObject("orderName", "코딩대모험 외 1권");
		mav.addObject("orderId", UUID.randomUUID().toString());
		mav.addObject("successUrl", "/order/process");
		mav.addObject("failUrl", "/order/fail");
		mav.addObject("customerEmail", "test@gmail.com");
		mav.addObject("customerName", "ㅇㅅㅇ");
		mav.addObject("amount", 4000);
		return mav;
	}

	@GetMapping("/process")
	public String processOrder(
		@RequestParam String paymentKey,
		@RequestParam String orderId,
		@RequestParam Integer amount,
		RedirectAttributes redirectAttributes
	) {
		TossConfirmRequest req = new TossConfirmRequest(paymentKey, orderId, amount);
		TossConfirmResponse resp = tossPaymentsService.confirm(req);
		redirectAttributes.addFlashAttribute("orderData", resp);
		return "redirect:/order/complete";
	}

	@GetMapping("/complete")
	public String orderCompletePage(@Valid @ModelAttribute("orderData") TossConfirmResponse data,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "order/fail";
		}

		return "order/complete";
	}

	@GetMapping("/fail")
	public String orderFailPage() {

		return "order/fail";
	}
}