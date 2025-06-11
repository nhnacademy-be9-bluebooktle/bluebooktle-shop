package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.frontend.service.RefundService;

@Slf4j
@Controller
@RequestMapping("/mypage/refunds")
@RequiredArgsConstructor
public class MypageRefundController {
	private final RefundService refundService;

	@PostMapping("/request")
	public String processRefundRequest(
		@Valid @ModelAttribute("refundRequest") RefundCreateRequest request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes
	) {
		if (bindingResult.hasErrors()) {
			return "redirect:/mypage/orders/" + request.orderKey();
		}
		try {
			refundService.requestRefund(request);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "환불 요청이 정상적으로 접수되었습니다.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getMessage());
		}
		return "redirect:/mypage/orders/" + request.orderKey();
	}
}
