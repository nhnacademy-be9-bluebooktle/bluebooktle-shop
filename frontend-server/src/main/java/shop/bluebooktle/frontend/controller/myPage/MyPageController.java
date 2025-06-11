package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

	@GetMapping
	public String myPageDefault() {
		return "redirect:/mypage/profile";
	}

	@GetMapping("/reviews")
	public String userReviewsPage() {

		return "mypage/review_list";
	}
}
