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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.frontend.service.AdminBookService;
import shop.bluebooktle.frontend.service.AdminCategoryService;
import shop.bluebooktle.frontend.service.AdminCouponService;

@Slf4j
@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

	private final AdminCouponService adminCouponService;
	private final AdminCategoryService adminCategoryService;
	private final AdminBookService adminBookService;

	@GetMapping("/type/new")
	public String showCouponTypeForm(Model model, HttpServletRequest request) {
		if (!model.containsAttribute("couponType")) {
			model.addAttribute("couponType", new CouponTypeRegisterRequest());
		}
		model.addAttribute("currentURI", request.getRequestURI());
		return "admin/coupon/coupon_type_form";
	}

	@PostMapping("/type")
	public String registerCouponType(@Valid @ModelAttribute("couponType") CouponTypeRegisterRequest request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {
		log.info("쿠폰 정책 요청: {}", request);

		if (bindingResult.hasErrors()) {
			log.warn("쿠폰 정책 저장 폼 유효성 검증 실패: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.couponType",
				bindingResult);
			redirectAttributes.addFlashAttribute("couponType", request);
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorCode", "VALIDATION_ERROR");
			redirectAttributes.addFlashAttribute("errorMessage", "입력값을 확인해주세요.");
			return "redirect:/admin/coupons/type/new";
		}

		adminCouponService.registerCouponType(request);
		redirectAttributes.addFlashAttribute("globalSuccessMessage", "쿠폰 정책 " + request.getName() + "이 성공적으로 등록되었습니다.");
		redirectAttributes.addFlashAttribute("globalSuccessTitle", "정책 등록 완료");
		return "redirect:/admin/coupons";
	}

	// 쿠폰 등록 페이지
	// TODO [쿠폰] target 값 유지하기
	// TODO [쿠폰] 도서 검색 기능 -> 기능 구현 완료 시 적용 예정
	@GetMapping("/new")
	public String showCouponForm(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		model.addAttribute("currentURI", request.getRequestURI());

		if (!model.containsAttribute("coupon")) {
			CouponRegisterRequest couponRequest = new CouponRegisterRequest();
			model.addAttribute("coupon", couponRequest);
		}

		// 쿠폰 정책 정보
		PaginationData<CouponTypeResponse> couponTypeData = adminCouponService.getAllCouponType();
		model.addAttribute("couponTypes", couponTypeData.getContent());

		// 도서 정보
		Page<BookAllResponse> books = adminBookService.getPagedBooks(page, size, searchKeyword);
		model.addAttribute("books", books.getContent());
		model.addAttribute("currentPageZeroBased", books.getNumber());
		model.addAttribute("totalPages", books.getTotalPages());
		model.addAttribute("totalElements", books.getTotalElements());
		model.addAttribute("currentSize", books.getSize());

		// 페이징 URL에 선택 정보 포함
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromPath(request.getRequestURI())
			.queryParam("size", size);

		if (StringUtils.hasText(searchKeyword)) {
			uriBuilder.queryParam("searchKeyword", searchKeyword);
		}

		model.addAttribute("baseUrlWithParams", uriBuilder.toUriString());

		// 선택값 View에서 다시 사용할 수 있게 추가
		model.addAttribute("searchKeyword", searchKeyword);

		// 카테고리 정보
		List<CategoryTreeResponse> categoryTree = adminCategoryService.getCategoryTree();
		model.addAttribute("categoryTree", categoryTree);

		return "admin/coupon/coupon_form";
	}

	@PostMapping
	public String registerCoupon(@Valid @ModelAttribute("coupon") CouponRegisterRequest request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.coupon", bindingResult);
			redirectAttributes.addFlashAttribute("coupon", request);
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorCode", "VALIDATION_ERROR");
			redirectAttributes.addFlashAttribute("errorMessage", "입력값을 확인해주세요.");
			return "redirect:/admin/coupons/new";
		}

		adminCouponService.registerCoupon(request);
		redirectAttributes.addFlashAttribute("globalSuccessMessage", "쿠폰이 성공적으로 등록되었습니다.");
		redirectAttributes.addFlashAttribute("globalSuccessTitle", "쿠폰 등록 완료");
		return "redirect:/admin/coupons";
	}

	@GetMapping
	public String getAllCoupon(Model model, HttpServletRequest request,
		@RequestParam(value = "issueCouponId", required = false) Long issueCouponId,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchField", required = false) String searchField,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@ModelAttribute("error") String error,
		@ModelAttribute("errorCode") String errorCode,
		@ModelAttribute("errorMessage") String errorMessage,
		@ModelAttribute("globalSuccessMessage") String globalSuccessMessage,
		@ModelAttribute("globalSuccessTitle") String globalSuccessTitle) {

		log.info("AdminCouponController - getAllCoupon: page={}, size={}, searchField={}, searchKeyword={}",
			page, size, searchField, searchKeyword);

		model.addAttribute("currentURI", request.getRequestURI());

		if ("true".equals(error)) {
			model.addAttribute("error", true);
			model.addAttribute("errorCode", errorCode);
			model.addAttribute("errorMessage", errorMessage);
		}
		if (globalSuccessMessage != null) {
			model.addAttribute("globalSuccessMessage", globalSuccessMessage);
			model.addAttribute("globalSuccessTitle", globalSuccessTitle);
		}

		PaginationData<CouponResponse> allCoupons = adminCouponService.getAllCoupon();
		List<CouponResponse> filteredCoupons = allCoupons.getContent().stream()
			.filter(coupon -> (searchKeyword == null || searchKeyword.trim().isEmpty()) ||
				(searchField == null || searchField.trim().isEmpty()) ||
				("couponName".equals(searchField) && coupon.getCouponName() != null &&
					coupon.getCouponName().toLowerCase().contains(searchKeyword.toLowerCase())))
			.collect(Collectors.toList());

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		Pageable pageable = PageRequest.of(page, size, sort);

		if (issueCouponId != null) {
			model.addAttribute("couponId", issueCouponId);
			model.addAttribute("issueCouponId", issueCouponId);
		}

		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), filteredCoupons.size());
		List<CouponResponse> pageContent =
			(start >= filteredCoupons.size() || start >= end) ? List.of() : filteredCoupons.subList(start, end);

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

		model.addAttribute("baseUrlWithParams", uriBuilder.toUriString());
		model.addAttribute("searchField", searchField);
		model.addAttribute("searchKeyword", searchKeyword);

		return "admin/coupon/coupon_list";
	}

	@PostMapping("/issue")
	public String issueCoupon(@Valid @ModelAttribute("userCoupon") UserCouponRegisterRequest request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {
		log.info("쿠폰 발급 요청: {}", request);

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userCoupon",
				bindingResult);
			redirectAttributes.addFlashAttribute("userCoupon", request);
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorCode", "VALIDATION_ERROR");
			redirectAttributes.addFlashAttribute("errorMessage", "입력값을 확인해주세요.");
			return "redirect:/admin/coupons?issueCouponId=" + request.getCouponId();
		}

		adminCouponService.issueCoupon(request);
		redirectAttributes.addFlashAttribute("globalSuccessMessage", "쿠폰이 성공적으로 발급되었습니다.");
		redirectAttributes.addFlashAttribute("globalSuccessTitle", "발급 완료");
		return "redirect:/admin/coupons";
	}

	@ExceptionHandler(ApplicationException.class)
	public String handleApplicationException(ApplicationException ex, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "true");
		redirectAttributes.addFlashAttribute("errorCode", ex.getErrorCode().getCode());
		redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		return "redirect:/admin/coupons";
	}
}
