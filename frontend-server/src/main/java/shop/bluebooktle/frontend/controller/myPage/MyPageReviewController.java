package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.request.ReviewRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.frontend.service.ReviewService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/reviews")
public class MyPageReviewController {

	private final ReviewService reviewService;

	//내가 쓴 리뷰 전체조회
	@GetMapping
	public String listMyReviews(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		Model model
	) {
		Long userId = userPrincipal.getUserId();
		Page<ReviewResponse> reviewsPage = reviewService.getMyReviews(userId, PageRequest.of(page, size));

		model.addAttribute("reviewsPage", reviewsPage);
		return "mypage/review_list";
	}

	//리뷰작성
	@PostMapping("/{bookOrderId}")
	public String addReview(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable Long bookOrderId,
		@ModelAttribute @Valid ReviewRequest reviewRequest,
		BindingResult bindingResult,
		RedirectAttributes redirectAttrs
	) {
		if (bindingResult.hasErrors()) {
			redirectAttrs.addFlashAttribute("globalErrorMessage", "리뷰 작성 실패!");
			return "redirect:/mypage/orders/" + bookOrderId;
		}

		Long userId = userPrincipal.getUserId();
		reviewService.addReview(userId, bookOrderId, reviewRequest);
		return "redirect:/mypage/orders/" + bookOrderId;
	}
}
