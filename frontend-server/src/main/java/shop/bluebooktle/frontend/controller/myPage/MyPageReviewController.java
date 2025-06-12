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
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.request.ReviewRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.frontend.service.ReviewService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/reviews")
public class MyPageReviewController {

	private final ReviewService reviewService;

	// 리뷰작성
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
			return "redirect:/mypage/reviews";
		}
		reviewService.addReview(userPrincipal.getUserId(), bookOrderId, reviewRequest);
		redirectAttrs.addFlashAttribute("globalSuccessMessage", "리뷰가 성공적으로 등록되었습니다.");
		return "redirect:/mypage/reviews";
	}

	// 내가 쓴 리뷰 전체조회
	@GetMapping
	public String getMyReviews(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size,
		Model model
	) {
		Long userId = userPrincipal.getUserId();
		Page<ReviewResponse> reviewsPage = reviewService.getMyReviews(userId, PageRequest.of(page, size));

		model.addAttribute("reviewsPage", reviewsPage);
		return "mypage/review_list";
	}

	@PostMapping("/edit/{reviewId}")
	public String updateReview(
		@PathVariable Long reviewId,
		@ModelAttribute @Valid ReviewRequest reviewRequest,
		BindingResult bindingResult,
		RedirectAttributes redirectAttrs
	) {
		if (bindingResult.hasErrors()) {
			redirectAttrs.addFlashAttribute("globalErrorMessage", "리뷰 수정 실패: 입력 값이 올바르지 않습니다.");
			return "redirect:/mypage/reviews";
		}
		try {
			reviewService.updateReivew(reviewId, reviewRequest);
			redirectAttrs.addFlashAttribute("globalSuccessMessage", "리뷰가 성공적으로 수정되었습니다.");
			return "redirect:/mypage/reviews";
		} catch (Exception e) {
			redirectAttrs.addFlashAttribute("globalErrorMessage", "리뷰 수정 중 오류가 발생했습니다: " + e.getMessage());
			return "redirect:/mypage/reviews";
		}
	}

}
