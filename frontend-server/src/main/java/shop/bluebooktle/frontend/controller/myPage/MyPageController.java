package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

	@GetMapping
	public String myPageDefault() {
		return "redirect:/mypage/profile";
	}
	
	@GetMapping("/orders")
	public String userOrdersPage() {
		return "mypage/order_list";
	}

	@GetMapping("/orders/{orderId}")
	public String orderDetailPage(@PathVariable String orderId) {
		System.out.println("Requesting order detail for (Mock): " + orderId);
		return "mypage/order_detail";
	}

	@GetMapping("/addresses")
	public String userAddressesPage() {
		return "mypage/address_list";
	}

	@GetMapping("/points")
	public String userPointsPage() {
		return "mypage/point_log";
	}

	@GetMapping("/coupons")
	public String userCouponsPage() {

		return "mypage/coupon_list";
	}

	@GetMapping("/reviews")
	public String userReviewsPage() {

		return "mypage/review_list";
	}

	@GetMapping("/likes")
	public String userWishlistPage() {

		return "mypage/likes_list";
	}
}