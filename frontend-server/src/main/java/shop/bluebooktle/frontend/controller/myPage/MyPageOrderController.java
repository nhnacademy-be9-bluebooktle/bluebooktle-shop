package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.frontend.service.OrderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/orders")
public class MyPageOrderController {

	private final OrderService orderService;

	@GetMapping
	public String userOrdersPage(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "7") int size,
		@RequestParam(value = "status", required = false) OrderStatus status,
		Model model
	) {
		PaginationData<OrderHistoryResponse> paginationData = orderService.getOrderHistory(page, size, status);

		model.addAttribute("ordersPage", paginationData);
		model.addAttribute("status", status);
		return "mypage/order_list";
	}

	@PostMapping("/{orderKey}/cancel")
	public String cancelOrder(
		@PathVariable String orderKey,
		RedirectAttributes redirectAttributes
	) {
		try {
			orderService.cancelOrder(orderKey);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getMessage());
		}
		return "redirect:/mypage/orders/" + orderKey;
	}

	@GetMapping("/{orderKey}")
	public ModelAndView orderDetailPage(@PathVariable String orderKey,
		RedirectAttributes redirectAttributes
	) {
		ModelAndView mav = new ModelAndView("mypage/order_detail");

		try {
			OrderDetailResponse orderDetails = orderService.getOrderDetailByOrderKey(orderKey);
			mav.addObject("order", orderDetails);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getMessage());
			mav.setViewName("redirect:/");
		}
		return mav;
	}
}
