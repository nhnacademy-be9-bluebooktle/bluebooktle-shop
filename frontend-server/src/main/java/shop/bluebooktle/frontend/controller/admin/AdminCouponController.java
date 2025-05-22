package shop.bluebooktle.frontend.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.frontend.dto.CouponRegisterFormDto;
import shop.bluebooktle.frontend.service.AdminCouponService;

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
		model.addAttribute("couponTypes", adminCouponService.getAllCouponTypes());
		// model.addAttribute("books", bookService.getAll()); //TODO [쿠폰] 도서 구현 시 작업시작
		// model.addAttribute("categories", categoryService.getAll()); //TODO [쿠폰] 카테고리 구현 시 작업시작
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

	@GetMapping
	public String getAllCoupon(Model model, HttpServletRequest request,
		@RequestParam(value = "issueCouponId", required = false) Long issueCouponId,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchField", required = false) String searchField,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

		model.addAttribute("currentURI", request.getRequestURI());

		PaginationData<CouponResponse> allCoupons = adminCouponService.getAllCoupons();

		// 검색 필터링
		List<CouponResponse> filteredCoupons = allCoupons.getContent().stream()
			.filter(coupon ->
				(searchKeyword == null || searchKeyword.trim().isEmpty()) ||
					(searchField == null || searchField.trim().isEmpty()) ||
					(searchField.equals("couponName") && coupon.getCouponName() != null &&
						coupon.getCouponName().toLowerCase().contains(searchKeyword.toLowerCase()))
			).collect(Collectors.toList());

		// 페이징
		Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
		Pageable pageable = PageRequest.of(page, size, sort);

		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), filteredCoupons.size());
		List<CouponResponse> pageContent =
			(start >= filteredCoupons.size() || start > end) ? List.of() : filteredCoupons.subList(start, end);

		Page<CouponResponse> couponPage = new PageImpl<>(pageContent, pageable, filteredCoupons.size());

		model.addAttribute("coupons", couponPage.getContent());
		model.addAttribute("currentPage", couponPage.getNumber());
		model.addAttribute("totalPages", couponPage.getTotalPages());
		model.addAttribute("currentSize", couponPage.getTotalElements());
		model.addAttribute("totalElements", couponPage.getTotalElements());

		if (issueCouponId != null) {
			model.addAttribute("issueCouponId", issueCouponId); // 폼 표시 조건
			model.addAttribute("couponId", issueCouponId);      // hidden input 용
		}

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", couponPage.getSize());
		if (searchField != null && !searchField.isEmpty())
			uriBuilder.queryParam("searchField", searchField);
		if (searchKeyword != null && !searchKeyword.isEmpty())
			uriBuilder.queryParam("searchKeyword", searchKeyword);
		model.addAttribute("baseUrlWithParams", uriBuilder.toUriString());

		model.addAttribute("searchField", searchField);
		model.addAttribute("searchKeyword", searchKeyword);

		return "admin/coupon/coupon_list";
	}

	@PostMapping("/issue")
	public String issueCoupon(@Valid @ModelAttribute("userCoupon") UserCouponRegisterRequest request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userCoupon",
				bindingResult);
			redirectAttributes.addFlashAttribute("userCoupon", request);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");

			return "redirect:/admin/coupons?issueCouponId=" + request.getCouponId();
		}

		try {
			adminCouponService.issueCoupon(request);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "쿠폰이 성공적으로 발급되었습니다.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "발급 중 오류 발생");
			redirectAttributes.addFlashAttribute("userCoupon", request);
			return "redirect:/admin/coupons?issueCouponId=" + request.getCouponId();
		}

		return "redirect:/admin/coupons";
	}

}