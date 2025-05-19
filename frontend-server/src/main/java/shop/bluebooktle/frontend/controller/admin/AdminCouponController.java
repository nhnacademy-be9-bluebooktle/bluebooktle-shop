package shop.bluebooktle.frontend.controller.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

	// private final AdminCouponService adminCouponService; // 실제 서비스 주입

	// --- DTO 정의 ---
	@Getter
	@Setter
	@ToString
	static class CouponDto {
		private Long id;
		private String name;
		private String couponCode;
		private String description;

		// @NotNull (jakarta.validation)
		private Long couponTypeId; // coupon_type 테이블 FK
		private String couponTypeName; // UI 표시용

		// @NotBlank
		private String discountType; // "AMOUNT", "PERCENTAGE"
		// @Positive
		private Double discountValue; // 금액 또는 비율
		private Double maxDiscountAmount; // 정률 할인 시 최대 할인 금액
		private Double minPurchaseAmount; // 최소 구매 금액

		// @NotBlank
		private String targetType; // "ALL_ORDERS", "SPECIFIC_BOOKS", "SPECIFIC_CATEGORIES"
		private List<Long> targetBookIds = new ArrayList<>();
		private List<String> targetBookNames = new ArrayList<>(); // UI 표시용
		private List<Long> targetCategoryIds = new ArrayList<>();
		private List<String> targetCategoryNames = new ArrayList<>(); // UI 표시용

		private LocalDateTime validFrom;
		private LocalDateTime validTo;
		private Integer issuanceValidDays; // 발급 후 유효 일수
		private boolean isActive = true;
		private LocalDateTime createdAt;

		// 임시 생성자
		public CouponDto() {
		}

		public CouponDto(Long id, String name, String couponCode, String couponTypeName, String discountType,
			Double discountValue, LocalDateTime validFrom, LocalDateTime validTo, boolean isActive) {
			this.id = id;
			this.name = name;
			this.couponCode = couponCode;
			this.couponTypeName = couponTypeName;
			this.discountType = discountType;
			this.discountValue = discountValue;
			this.validFrom = validFrom;
			this.validTo = validTo;
			this.isActive = isActive;
			this.createdAt = LocalDateTime.now().minusDays(id == null ? 0 : id);
		}
	}

	@Getter
	@Setter
	static class CouponSearchCriteria {
		private String searchKeywordType = "name"; // 기본 검색 조건: 쿠폰명
		private String searchKeyword;
		private String status; // "active", "inactive", "expired", "" (전체)
		private int page = 1;
		private int size = 10;
	}

	@Getter
	@Setter
	static class SelectOption { // 폼 내 셀렉트 박스 옵션용
		private String value;
		private String text;

		public SelectOption(String value, String text) {
			this.value = value;
			this.text = text;
		}
	}

	@GetMapping
	public String listCoupons(@ModelAttribute("searchCriteria") CouponSearchCriteria searchCriteria,
		Model model, HttpServletRequest request) {
		log.info("어드민 쿠폰 정책 목록 페이지 요청. URI: {}, 검색조건: {}", request.getRequestURI(), searchCriteria);
		model.addAttribute("pageTitle", "쿠폰 정책 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		// TODO: adminCouponService.getCoupons(searchCriteria) 호출
		// 임시 데이터
		List<CouponDto> coupons = Arrays.asList(
			new CouponDto(1L, "신규회원 10% 할인", "WELCOME10", "신규가입 쿠폰", "PERCENTAGE", 10.0,
				LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(20), true),
			new CouponDto(2L, "봄맞이 5,000원 할인", "SPRING5000", "정기 프로모션", "AMOUNT", 5000.0,
				LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(5), true),
			new CouponDto(3L, "IT 도서 특별 할인", "ITBOOK20", "카테고리 쿠폰", "PERCENTAGE", 20.0,
				LocalDateTime.now().minusDays(30), LocalDateTime.now().minusDays(1), false) // 만료된 쿠폰 예시
		);
		model.addAttribute("coupons", coupons);

		// 페이징 정보 (commonPagination 프래그먼트 사용을 위함)
		int currentPageForModel = searchCriteria.getPage() > 0 ? searchCriteria.getPage() - 1 : 0; // 0-based
		int totalPagesForModel = 1; // 실제 페이징 처리 시 동적으로 계산 ( (coupons.size() + size -1) / size )
		model.addAttribute("currentPage", currentPageForModel);
		model.addAttribute("totalPages", totalPagesForModel);

		// baseUrlWithParams 및 searchKeyword 설정 (commonPagination 프래그먼트용)
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(request.getRequestURI())
			.queryParam("size", searchCriteria.getSize())
			.queryParam("searchKeywordType", searchCriteria.getSearchKeywordType())
			.queryParam("status", searchCriteria.getStatus());

		model.addAttribute("baseUrlWithParams", builder.toUriString());
		model.addAttribute("searchKeyword", searchCriteria.getSearchKeyword());

		// 검색 필터 옵션
		model.addAttribute("searchKeywordTypes", Map.of("name", "쿠폰명", "couponCode", "쿠폰코드"));
		model.addAttribute("statusOptions", Arrays.asList(
			new SelectOption("", "전체 상태"),
			new SelectOption("active", "활성"),
			new SelectOption("inactive", "비활성"),
			new SelectOption("expired", "만료")
		));

		return "admin/coupon/coupon_list";
	}

	@GetMapping({"/new", "/{couponId}/edit"})
	public String couponForm(@PathVariable(value = "couponId", required = false) Long couponId,
		Model model, HttpServletRequest request) {
		log.info("어드민 쿠폰 폼 페이지 요청. URI: {}, couponId: {}", request.getRequestURI(), couponId);
		model.addAttribute("currentURI", request.getRequestURI());

		CouponDto couponDto;
		String pageTitle;

		if (couponId != null) {
			pageTitle = "쿠폰 정보 수정 (ID: " + couponId + ")";
			// TODO: adminCouponService.getCouponById(couponId) 호출
			// 임시 데이터 (수정 시)
			couponDto = new CouponDto(couponId, "수정용 쿠폰", "EDITME", "정기 프로모션", "AMOUNT", 3000.0, LocalDateTime.now(),
				LocalDateTime.now().plusMonths(1), true);
			couponDto.setDescription("이 쿠폰은 테스트 수정용입니다.");
			couponDto.setMinPurchaseAmount(30000.0);
			couponDto.setTargetType("ALL_ORDERS");
			couponDto.setCouponTypeId(1L); // 임시 ID
		} else {
			pageTitle = "새 쿠폰 등록";
			couponDto = new CouponDto(); // 기본값 설정된 빈 객체
			couponDto.setCouponCode("자동생성예정"); // 예시
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("coupon", couponDto);

		// 폼 선택 옵션들
		// TODO: 실제 서비스에서 CouponType, Category, Book 목록 등 조회하여 전달
		model.addAttribute("couponTypes",
			Arrays.asList(new SelectOption("1", "정기 프로모션"), new SelectOption("2", "신규가입 쿠폰"),
				new SelectOption("3", "카테고리 쿠폰")));
		model.addAttribute("discountTypes",
			Arrays.asList(new SelectOption("AMOUNT", "정액 할인"), new SelectOption("PERCENTAGE", "정률 할인")));
		model.addAttribute("targetTypes", Arrays.asList(
			new SelectOption("ALL_ORDERS", "전체 주문"),
			new SelectOption("SPECIFIC_BOOKS", "특정 도서"),
			new SelectOption("SPECIFIC_CATEGORIES", "특정 카테고리")
		));
		// 특정 도서/카테고리 선택을 위한 데이터 (실제로는 검색 기능 등을 통해 동적으로 로드)
		model.addAttribute("availableBooks",
			Arrays.asList(new SelectOption("101", "자바의 정석"), new SelectOption("102", "스프링 부트 실전 활용")));
		model.addAttribute("availableCategories",
			Arrays.asList(new SelectOption("1", "국내도서 > 소설"), new SelectOption("2", "컴퓨터/IT > 프로그래밍")));

		return "admin/coupon/coupon_form";
	}

	@PostMapping("/save")
	public String saveCoupon(//@Valid @ModelAttribute("coupon")
		@ModelAttribute("coupon") CouponDto couponDto, // @Valid는 DTO에 유효성 어노테이션 추가 후 사용
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("쿠폰 저장 요청: {}", couponDto);

		// TODO: 서버 사이드 유효성 검사 (예: couponDto.getName().isBlank(), 날짜 유효성 등)
		// if (couponDto.getName() == null || couponDto.getName().trim().isEmpty()) {
		//     bindingResult.rejectValue("name", "NotEmpty", "쿠폰 이름은 필수입니다.");
		// }
		// ... 기타 유효성 검사 ...

		if (bindingResult.hasErrors()) {
			log.warn("쿠폰 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			// 오류 메시지 및 입력값 유지를 위해 FlashAttribute 사용
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.coupon", bindingResult);
			redirectAttributes.addFlashAttribute("coupon", couponDto);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");

			// 폼 페이지로 리다이렉트 시 필요한 선택 옵션들을 다시 로드해야 하므로,
			// GET 핸들러에서 처리하도록 유도하거나, 여기서 다시 모델에 담아 리다이렉트
			// (하지만 RedirectAttributes는 GET 파라미터로만 전달되므로 복잡 객체는 어려움)
			// 따라서, 폼으로 다시 forward 하거나, GET 요청으로 리다이렉트하고 GET 핸들러에서 처리.
			// 여기서는 PRG 패턴을 위해 리다이렉트. (GET 핸들러가 폼 옵션들을 항상 로드하도록)
			if (couponDto.getId() != null) {
				return "redirect:/admin/coupons/" + couponDto.getId() + "/edit";
			} else {
				return "redirect:/admin/coupons/new";
			}
		}

		try {
			// TODO: 실제 서비스 로직 호출 (adminCouponService.saveCoupon(couponDto))
			// if (couponDto.getId() == null) { 쿠폰 생성 } else { 쿠폰 수정 }

			String action = (couponDto.getId() == null) ? "등록" : "수정";
			log.info("쿠폰 {} 성공 (임시): {}", action, couponDto.getName());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"쿠폰 '" + couponDto.getName() + "' 정보가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) { // 실제로는 구체적인 예외 (ApplicationException 등) 처리
			log.error("쿠폰 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "쿠폰 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("coupon", couponDto); // 오류 시에도 입력값 유지
			if (couponDto.getId() != null) {
				return "redirect:/admin/coupons/" + couponDto.getId() + "/edit";
			} else {
				return "redirect:/admin/coupons/new";
			}
		}
		return "redirect:/admin/coupons";
	}

	@PostMapping("/{couponId}/delete")
	public String deleteCoupon(@PathVariable Long couponId, RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("쿠폰 삭제 요청: ID {}", couponId);
		try {
			// TODO: 실제 삭제 로직 (adminCouponService.deleteCoupon(couponId))
			// 삭제는 실제 데이터를 지우거나, isActive=false 등으로 처리할 수 있음. 정책에 따라.
			log.info("임시 쿠폰 삭제 성공 처리: ID {}", couponId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "쿠폰(ID: " + couponId + ")이 성공적으로 삭제되었습니다.");
		} catch (Exception e) {
			log.error("쿠폰 삭제 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "쿠폰 삭제 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/coupons";
	}
}