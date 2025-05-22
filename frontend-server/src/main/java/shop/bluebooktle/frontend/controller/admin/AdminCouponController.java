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
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.frontend.service.AdminCouponService;

@Slf4j
@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

	private final AdminCouponService adminCouponService;
	// private final BookService bookService; //TODO [쿠폰] 도서 List 용도
	// private final CategoryService categoryService; //TODO 카테고리 List 용도

	/* 쿠폰 정책 등 */
	@GetMapping("/type/new")
	public String showCouponTypeForm(Model model, HttpServletRequest request) {
		if (!model.containsAttribute("couponType")) {
			model.addAttribute("couponType", new CouponTypeRegisterRequest());
		}
		model.addAttribute("currentURI", request.getRequestURI());
		return "admin/coupon/coupon_type_form";
	}

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

			return "redirect:/admin/coupons/type/new";
		}

		try {
			adminCouponService.registerCouponType(request);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"쿠폰 정책 " + request.getName() + "이 성공적으로 등록되었습니다.");
		} catch (Exception e) {
			log.error("쿠폰 정책 등록 실패", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "저장 중 오류 발생");
			redirectAttributes.addFlashAttribute("couponType", request);
			return "redirect:/admin/coupons/type/new";
		}

		return "redirect:/admin/coupons";
	}

	/* 쿠폰 등록*/
	@GetMapping("/new")
	public String showCouponForm(Model model, HttpServletRequest request) {
		model.addAttribute("currentURI", request.getRequestURI());

		if (!model.containsAttribute("coupon")) {
			model.addAttribute("coupon", new CouponRegisterRequest());
		}

		//쿠폰 정책 목록
		PaginationData<CouponTypeResponse> couponTypeData = adminCouponService.getAllCouponType();
		model.addAttribute("couponTypes", couponTypeData.getContent());

		// 도서 목록
		// model.addAttribute("books", bookService.getAll()); //TODO [쿠폰] bookService 완성 후 작업
		// 카테고리 목록
		// model.addAttribute("categories", categoryService.getAll()); //TODO [쿠폰] categoryService 완성 후 작업
		return "admin/coupon/coupon_form";
	}

	@PostMapping
	public String registerCoupon(
		@Valid @ModelAttribute("coupon") CouponRegisterRequest request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.coupon", bindingResult);
			redirectAttributes.addFlashAttribute("coupon", request);
			// model.addAttribute("books", bookService.getAll());
			// model.addAttribute("categories", categoryService.getAll());
			return "/admin/coupon/coupon_form";
		}

		try {
			adminCouponService.registerCoupon(request);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "쿠폰이 성공적으로 등록되었습니다.");
			return "redirect:/admin/coupons";
		} catch (Exception e) {
			log.error("쿠폰 등록 실패", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "쿠폰 등록 중 오류 발생");
			redirectAttributes.addFlashAttribute("coupon", request);
			return "redirect:/admin/coupons/new";
		}
	}

	// 쿠폰 전체 조회
	@GetMapping
	public String getAllCoupon(Model model, HttpServletRequest request,
		@RequestParam(value = "issueCouponId", required = false) Long issueCouponId,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchField", required = false) String searchField,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

		log.info("AdminCouponController - getAllCoupon: page={}, size={}, searchField={}, searchKeyword={}",
			page, size, searchField, searchKeyword);

		model.addAttribute("currentURI", request.getRequestURI());

		PaginationData<CouponResponse> allCoupons = adminCouponService.getAllCoupon();
		log.info("전체 쿠폰 개수: {}", allCoupons.getContent().size());

		//검색 필터링
		List<CouponResponse> filteredCoupons = allCoupons.getContent().stream()
			.filter(coupon ->
				(searchKeyword == null || searchKeyword.trim().isEmpty()) ||
					(searchField == null || searchField.trim().isEmpty()) ||
					(searchField.equals("couponName") && coupon.getCouponName() != null &&
						coupon.getCouponName().toLowerCase().contains(searchKeyword.toLowerCase()))
			).collect(Collectors.toList());

		//페이징
		Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
		Pageable pageable = PageRequest.of(page, size, sort);

		if (issueCouponId != null) {
			model.addAttribute("couponId", issueCouponId);       // 모달 내부 form에 쓸 쿠폰 ID
			model.addAttribute("issueCouponId", issueCouponId);  // 모달 자체를 띄우기 위한 조건
		}

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

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", couponPage.getSize());
		if (searchField != null && !searchField.isEmpty())
			uriBuilder.queryParam("searchField", searchField);
		if (searchKeyword != null && !searchKeyword.isEmpty())
			uriBuilder.queryParam("searchKeyword", searchKeyword);

		String baseUrlWithParams = uriBuilder.toUriString();
		model.addAttribute("baseUrlWithParams", baseUrlWithParams);

		//검색 상태 유지
		model.addAttribute("searchField", searchField);
		model.addAttribute("searchKeyword", searchKeyword);

		return "admin/coupon/coupon_list";
	}

	// 쿠폰 발급
	@PostMapping("/issue")
	public String issueCoupon(
		@Valid @ModelAttribute("userCoupon") UserCouponRegisterRequest request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes
	) {
		log.info("쿠폰 발급 요청: {}", request);

		if (bindingResult.hasErrors()) {
			log.warn("쿠폰 발급 폼 유효성 검증 실패: {}", bindingResult.getAllErrors());

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
			log.error("쿠폰 발급 실패", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "발급 중 오류 발생");
			redirectAttributes.addFlashAttribute("userCoupon", request);
			return "redirect:/admin/coupons?issueCouponId=" + request.getCouponId();
		}

		return "redirect:/admin/coupons";
	}
}