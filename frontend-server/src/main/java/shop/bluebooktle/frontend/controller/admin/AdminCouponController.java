package shop.bluebooktle.frontend.controller.admin;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;
import shop.bluebooktle.common.domain.coupon.CouponIssueType;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
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
	@GetMapping("/new")
	public String showCouponForm(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

		// 현재 URI
		model.addAttribute("currentURI", request.getRequestURI());

		// 쿠폰 폼 객체 초기화
		if (!model.containsAttribute("coupon")) {
			model.addAttribute("coupon", new CouponRegisterRequest());
		}

		// 쿠폰 정책 목록
		PaginationData<CouponTypeResponse> couponTypeData = adminCouponService.getAllCouponType(
			PageRequest.of(page, size));
		model.addAttribute("couponTypeData", couponTypeData);

		// 도서 목록
		var books = adminBookService.getPagedBooksByAdmin(page, size, searchKeyword);
		model.addAttribute("books", books.getContent());
		model.addAttribute("currentPageZeroBased", books.getNumber());
		model.addAttribute("totalPages", books.getTotalPages());
		model.addAttribute("totalElements", books.getTotalElements());
		model.addAttribute("currentSize", books.getSize());

		// 검색 및 페이징 URL 생성
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromPath(request.getRequestURI())
			.queryParam("size", size);
		if (StringUtils.hasText(searchKeyword)) {
			uriBuilder.queryParam("searchKeyword", searchKeyword);
		}
		model.addAttribute("baseUrlWithParams", uriBuilder.toUriString());
		model.addAttribute("searchKeyword", searchKeyword);
		List<CategoryTreeResponse> categoryTree = adminCategoryService.getCategoryTree();
		model.addAttribute("categoryTree", categoryTree);

		return "admin/coupon/coupon_form";
	}

	@GetMapping("/book-fragment")
	public String getBookFragment(Model model,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String searchKeyword) {

		var books = adminBookService.getPagedBooksByAdmin(page, size, searchKeyword);

		model.addAttribute("books", books.getContent());
		model.addAttribute("totalPages", books.getTotalPages());
		model.addAttribute("currentPageZeroBased", books.getNumber());
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("baseUrlWithParams", "/admin/coupons/book-fragment");

		return "admin/coupon/book_list :: bookList";
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
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@ModelAttribute("error") String error,
		@ModelAttribute("errorCode") String errorCode,
		@ModelAttribute("errorMessage") String errorMessage,
		@ModelAttribute("globalSuccessMessage") String globalSuccessMessage,
		@ModelAttribute("globalSuccessTitle") String globalSuccessTitle) {

		log.info("AdminCouponController - getAllCoupon: page={}, size={}, searchKeyword={}", page, size, searchKeyword);

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

		Pageable pageable = PageRequest.of(page, size);

		PaginationData<CouponResponse> coupons = adminCouponService.getAllCoupon(pageable, searchKeyword);
		model.addAttribute("coupons", coupons);

		if (issueCouponId != null) {
			model.addAttribute("couponId", issueCouponId);
			model.addAttribute("issueCouponId", issueCouponId);
		}

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", size);
		if (searchKeyword != null && !searchKeyword.isEmpty()) {
			uriBuilder.queryParam("searchKeyword", searchKeyword);
		}

		model.addAttribute("baseUrlWithParams", uriBuilder.toUriString());
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

	@GetMapping("/failed")
	public String showFailedCoupons(
		@RequestParam(required = false) CouponIssueType type,
		@RequestParam(required = false) CouponIssueStatus status,
		@PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
		Model model,
		HttpServletRequest request
	) {
		model.addAttribute("currentURI", request.getRequestURI());

		var searchRequest = FailedCouponIssueSearchRequest.builder()
			.type(type)
			.status(status)
			.build();

		var result = adminCouponService.getAllFailedCouponIssue(pageable, searchRequest);

		model.addAttribute("coupons", result.getContent());
		model.addAttribute("pagination", result.getPagination());
		model.addAttribute("searchRequest", searchRequest);

		return "admin/coupon/failed_coupon_list";
	}

	@PostMapping("/failed/{issueId}/resend")
	public String resendFailedCoupon(
		@PathVariable Long issueId,
		RedirectAttributes redirectAttributes) {
		adminCouponService.resendFailedCoupon(issueId);
		redirectAttributes.addFlashAttribute("globalSuccessMessage", "선택한 쿠폰을 재발급 시도합니다.");
		redirectAttributes.addFlashAttribute("globalSuccessTitle", "재발급 시도 완료");
		return "redirect:/admin/coupons/failed";
	}

	@PostMapping("/failed/resend-all")
	public String resendAllFailedCoupons(RedirectAttributes redirectAttributes) {
		adminCouponService.resendAllFailedCoupons();
		redirectAttributes.addFlashAttribute("globalSuccessMessage", "모든 실패한 쿠폰을 다시 시도합니다.");
		redirectAttributes.addFlashAttribute("globalSuccessTitle", "재발급 시도 완료");
		return "redirect:/admin/coupons/failed";
	}

	@ExceptionHandler(ApplicationException.class)
	public String handleApplicationException(ApplicationException ex, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "true");
		redirectAttributes.addFlashAttribute("errorCode", ex.getErrorCode().getCode());
		redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		return "redirect:/admin/coupons";
	}
}
