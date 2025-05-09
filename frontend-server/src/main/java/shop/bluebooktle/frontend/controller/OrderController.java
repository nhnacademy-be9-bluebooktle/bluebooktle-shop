package shop.bluebooktle.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
public class OrderController {

	@GetMapping("/checkout")
	public String checkoutPage() {

		return "order/checkout";
	}

	@PostMapping("/process")
	public String processOrder() {
		System.out.println("Mock 주문 처리 시뮬레이션 완료!");
		// 주문 ID 등을 생성하여 완료 페이지로 전달할 수 있음 (현재는 생략)
		return "redirect:/order/complete";
	}

	@GetMapping("/complete")
	public String orderCompletePage() {
		// 실제로는 모델에 주문 완료 정보(주문번호 등)를 담아 전달
		return "order/complete";
	}
}