package shop.bluebooktle.frontend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.frontend.dto.CouponRegisterFormDto;
import shop.bluebooktle.frontend.service.admin.AdminCouponService;

@Slf4j
@Controller
@RequestMapping("/admin/coupon")
@RequiredArgsConstructor
public class AdminCouponController {

	private final AdminCouponService adminCouponService;
	// private final BookService bookService; //TODO [쿠폰] 도서 List 용도
	// private final CategoryService categoryService; //TODO 카테고리 List 용도

	@GetMapping("/type/new")
	public String showCouponTypeForm(Model model) {
		if (!model.containsAttribute("couponType")) {
			model.addAttribute("couponType", new CouponTypeRegisterRequest());
		}
		return "admin/coupon/coupon_type_form";
	}

	// 쿠폰 정책 등록
	@PostMapping("/type")
	public String registerCouponType(
		@Valid @ModelAttribute("couponType") CouponTypeRegisterRequest request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {
		log.info("쿠폰 정책 요청: {}", request);

		if (bindingResult.hasErrors()) {
			log.warn("쿠폰 정책 저장 폼 유효성 검증 실패: {}", bindingResult.getAllErrors());

			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.couponType",
				bindingResult);
			redirectAttributes.addFlashAttribute("couponType", request);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");

			return "redirect:/admin/coupon/type/new";
		}

		try {
			adminCouponService.registerCouponType(request);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"쿠폰 정책 " + request.getName() + "이 성공적으로 등록되었습니다.");
		} catch (Exception e) {
			log.error("쿠폰 정책 등록 실패", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "저장 중 오류 발생");
			redirectAttributes.addFlashAttribute("couponType", request);
			return "redirect:/admin/coupon/type/new";
		}

		return "redirect:/admin/coupon";
	}

	@GetMapping("/new")
	public String showCouponForm(Model model) {
		model.addAttribute("coupon", new CouponRegisterRequest());
		model.addAttribute("couponTypes", adminCouponService.getCouponTypes());
		// model.addAttribute("books", bookService.getAll()); //TODO
		// model.addAttribute("categories", categoryService.getAll()); //TODO
		return "admin/coupon/coupon_form";
	}

	// 쿠폰 등록
	@PostMapping
	public String registerCoupon(
		@Valid @ModelAttribute("coupon") CouponRegisterFormDto form,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes
	) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.coupon", bindingResult);
			redirectAttributes.addFlashAttribute("coupon", form);
			return "redirect:/admin/coupons/new";
		}

		try {
			CouponRegisterRequest request = form.toRequest();
			adminCouponService.registerCoupon(request);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "쿠폰이 성공적으로 등록되었습니다.");
			return "redirect:/admin/coupon";
		} catch (Exception e) {
			log.error("쿠폰 등록 실패", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "쿠폰 등록 중 오류 발생");
			redirectAttributes.addFlashAttribute("coupon", form);
			return "redirect:/admin/coupon/new";
		}
	}

	// TODO [쿠폰] 쿠폰 조회
	// @GetMapping
	// public
	// TODO [쿠폰] 쿠폰 정책 조회
	// TODO [쿠폰]

}