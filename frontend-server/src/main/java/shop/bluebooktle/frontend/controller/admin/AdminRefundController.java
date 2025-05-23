package shop.bluebooktle.frontend.controller.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/admin/refunds")
@RequiredArgsConstructor
public class AdminRefundController {

	// private final AdminRefundService adminRefundService; // 실제 서비스

	// --- DTO 정의 ---
	@Getter
	@Setter
	@ToString
	static class RefundListDto {
		private Long refundId;
		private Long orderId;
		private String orderNumber;
		private Long userId;
		private String userNickname;
		private LocalDateTime requestDate;
		private Double refundAmount;
		private String status; // "REQUESTED", "PROCESSING", "COMPLETED", "REJECTED"

		public RefundListDto(Long refundId, String orderNumber, String userNickname, LocalDateTime requestDate,
			Double refundAmount, String status) {
			this.refundId = refundId;
			this.orderNumber = orderNumber;
			this.userNickname = userNickname;
			this.requestDate = requestDate;
			this.refundAmount = refundAmount;
			this.status = status;
		}
	}

	@Getter
	@Setter
	@ToString
	static class RefundDetailDto extends RefundListDto {
		private String reason;
		private String rejectionReason;
		private LocalDateTime completedDate;
		private String bankName;
		private String accountNumber;
		private String accountHolder;
		// 실제로는 주문 상세 정보, 주문 상품 정보 등 더 많은 내용이 필요할 수 있음
		private String ordererName; // 주문자명 (주문 정보에서 가져옴)
		private List<String> refundedItems = new ArrayList<>(); // 환불 대상 상품명 목록 (예시)

		public RefundDetailDto(Long refundId, String orderNumber, String userNickname, LocalDateTime requestDate,
			Double refundAmount, String status, String reason) {
			super(refundId, orderNumber, userNickname, requestDate, refundAmount, status);
			this.reason = reason;
		}
	}

	@Getter
	@Setter
	@ToString
	static class RefundSearchCriteria {
		private String searchOrderNumber;
		private String searchUser;
		private String filterStatus = "ALL";
		private LocalDate requestStartDate;
		private LocalDate requestEndDate;
		private int page = 1;
		private int size = 10;
	}

	// 임시 데이터 저장소
	private static final List<RefundDetailDto> refundStore = new ArrayList<>(Arrays.asList(
		new RefundDetailDto(1L, "ORD20250510001", "불만고객", LocalDateTime.now().minusDays(3), 35000.0, "REQUESTED",
			"상품 설명과 다름, 사이즈 미스"),
		new RefundDetailDto(2L, "ORD20250508007", "신중구매자", LocalDateTime.now().minusDays(7), 12000.0, "PROCESSING",
			"단순 변심"),
		new RefundDetailDto(3L, "ORD20250420015", "빠른포기", LocalDateTime.now().minusDays(10), 89000.0, "COMPLETED",
			"배송 지연으로 인한 취소") {{
			setCompletedDate(LocalDateTime.now().minusDays(8));
			setBankName("국민은행");
			setAccountNumber("123-456-7890");
			setAccountHolder("홍길동");
		}},
		new RefundDetailDto(4L, "ORD20250501003", "까다로운분", LocalDateTime.now().minusDays(5), 5500.0, "REJECTED",
			"제품 포장 개봉 및 사용 흔적") {{
			setRejectionReason("사용 흔적으로 인해 환불 불가");
		}}
	));
	private static Long nextRefundId = 5L;

	private String getStatusDisplayName(String statusKey) {
		Map<String, String> statusMap = Map.of(
			"REQUESTED", "환불 요청",
			"PROCESSING", "처리 중",
			"COMPLETED", "환불 완료",
			"REJECTED", "환불 거절"
		);
		return statusMap.getOrDefault(statusKey, statusKey);
	}

	@GetMapping
	public String listRefunds(@ModelAttribute("searchCriteria") RefundSearchCriteria criteria,
		Model model, HttpServletRequest request) {
		log.info("어드민 환불 목록 페이지 요청. URI: {}, 검색조건: {}", request.getRequestURI(), criteria);
		model.addAttribute("pageTitle", "환불 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		// TODO: adminRefundService.getRefunds(criteria) 호출
		// 임시 필터링 로직
		List<RefundDetailDto> filteredRefunds = refundStore.stream()
			.filter(r -> (criteria.getSearchOrderNumber() == null || criteria.getSearchOrderNumber().isEmpty()
				|| r.getOrderNumber().contains(criteria.getSearchOrderNumber())))
			.filter(r -> (criteria.getSearchUser() == null || criteria.getSearchUser().isEmpty() || r.getUserNickname()
				.toLowerCase()
				.contains(criteria.getSearchUser().toLowerCase())))
			.filter(r -> {
				if ("ALL".equals(criteria.getFilterStatus()))
					return true;
				return r.getStatus().equals(criteria.getFilterStatus());
			})
			.filter(r -> (criteria.getRequestStartDate() == null || !r.getRequestDate()
				.toLocalDate()
				.isBefore(criteria.getRequestStartDate())))
			.filter(r -> (criteria.getRequestEndDate() == null || !r.getRequestDate()
				.toLocalDate()
				.isAfter(criteria.getRequestEndDate())))
			.sorted((r1, r2) -> r2.getRequestDate().compareTo(r1.getRequestDate()))
			.collect(Collectors.toList());

		int start = (criteria.getPage() - 1) * criteria.getSize();
		int end = Math.min(start + criteria.getSize(), filteredRefunds.size());
		List<RefundListDto> paginatedRefunds = (start <= end) ?
			filteredRefunds.subList(start, end).stream()
				.map(
					detail -> new RefundListDto(detail.getRefundId(), detail.getOrderNumber(), detail.getUserNickname(),
						detail.getRequestDate(), detail.getRefundAmount(), detail.getStatus()))
				.collect(Collectors.toList())
			: new ArrayList<>();

		model.addAttribute("refunds", paginatedRefunds);

		int totalPages = (int)Math.ceil((double)filteredRefunds.size() / criteria.getSize());
		if (totalPages == 0 && filteredRefunds.size() > 0)
			totalPages = 1;

		model.addAttribute("currentPage", criteria.getPage() - 1);
		model.addAttribute("totalPages", totalPages);

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(request.getRequestURI())
			.queryParam("size", criteria.getSize())
			.queryParam("searchOrderNumber", criteria.getSearchOrderNumber())
			.queryParam("searchUser", criteria.getSearchUser())
			.queryParam("filterStatus", criteria.getFilterStatus())
			.queryParam("requestStartDate",
				criteria.getRequestStartDate() != null ? criteria.getRequestStartDate().toString() : "")
			.queryParam("requestEndDate",
				criteria.getRequestEndDate() != null ? criteria.getRequestEndDate().toString() : "");
		model.addAttribute("baseUrlWithParams", builder.toUriString());
		// commonPagination에 searchKeyword가 필요하지만, 이 페이지는 여러 검색 필드가 있으므로 직접 페이징 UI 구성 또는 commonPagination 수정 필요.
		// 여기서는 직접 페이징 UI 구현 가정.

		// model.addAttribute("statusOptions", Arrays.asList(
		// 	new AdminCouponController.SelectOption("ALL", "전체 상태"),
		// 	new AdminCouponController.SelectOption("REQUESTED", "환불 요청"),
		// 	new AdminCouponController.SelectOption("PROCESSING", "처리 중"),
		// 	new AdminCouponController.SelectOption("COMPLETED", "환불 완료"),
		// 	new AdminCouponController.SelectOption("REJECTED", "환불 거절")
		// ));
		model.addAttribute("getStatusDisplayName",
			(java.util.function.Function<String, String>)this::getStatusDisplayName);

		return "admin/refund/refund_list";
	}

	@GetMapping("/{refundId}")
	public String viewRefund(@PathVariable Long refundId, Model model, HttpServletRequest request) {
		log.info("어드민 환불 상세 페이지 요청. URI: {}, refundId: {}", request.getRequestURI(), refundId);
		model.addAttribute("currentURI", request.getRequestURI()); // 사이드바 액티브용

		// TODO: adminRefundService.getRefundDetail(refundId) 호출
		Optional<RefundDetailDto> refundOpt = refundStore.stream()
			.filter(r -> r.getRefundId().equals(refundId))
			.findFirst();

		if (refundOpt.isPresent()) {
			RefundDetailDto refundDetail = refundOpt.get();
			// 임시로 주문자명, 환불 상품 목록 채우기
			refundDetail.setOrdererName("주문고객" + refundDetail.getUserId());
			if (refundDetail.getRefundedItems().isEmpty()) {
				refundDetail.getRefundedItems().add("환불 상품 A (2개)");
				refundDetail.getRefundedItems().add("환불 상품 B (1개)");
			}

			model.addAttribute("refund", refundDetail);
			model.addAttribute("pageTitle", "환불 상세 (ID: " + refundId + ")");
			// 상태 변경 시 선택 가능한 다음 상태 목록 (예시)
			// List<AdminCouponController.SelectOption> nextStatusOptions = new ArrayList<>();
			// switch (refundDetail.getStatus()) {
			// 	case "REQUESTED":
			// 		nextStatusOptions.add(new AdminCouponController.SelectOption("PROCESSING", "처리 중으로 변경"));
			// 		nextStatusOptions.add(new AdminCouponController.SelectOption("COMPLETED", "즉시 완료로 변경"));
			// 		nextStatusOptions.add(new AdminCouponController.SelectOption("REJECTED", "거절로 변경"));
			// 		break;
			// 	case "PROCESSING":
			// 		nextStatusOptions.add(new AdminCouponController.SelectOption("COMPLETED", "완료로 변경"));
			// 		nextStatusOptions.add(new AdminCouponController.SelectOption("REJECTED", "거절로 변경"));
			// 		break;
			// 	// COMPLETED, REJECTED 상태에서는 더 이상 변경 불가 (예시 로직)
			// }
			// model.addAttribute("nextStatusOptions", nextStatusOptions);
			model.addAttribute("getStatusDisplayName",
				(java.util.function.Function<String, String>)this::getStatusDisplayName);

			return "admin/refund/refund_detail";
		} else {
			log.warn("요청한 환불 ID를 찾을 수 없음: {}", refundId);
			// RedirectAttributes 등을 사용하여 목록 페이지에 오류 메시지 전달 후 리다이렉트
			return "redirect:/admin/refunds";
		}
	}

	@PostMapping("/{refundId}/update-status")
	public String updateRefundStatus(@PathVariable Long refundId,
		@RequestParam String status,
		@RequestParam(required = false) String rejectionReason, // 거절 시에만 사용
		RedirectAttributes redirectAttributes) {
		log.info("환불 상태 변경 요청. ID: {}, 새 상태: {}, 거절사유: {}", refundId, status, rejectionReason);
		// TODO: adminRefundService.updateRefundStatus(refundId, status, rejectionReason) 호출
		Optional<RefundDetailDto> refundOpt = refundStore.stream()
			.filter(r -> r.getRefundId().equals(refundId))
			.findFirst();
		if (refundOpt.isPresent()) {
			RefundDetailDto refund = refundOpt.get();
			refund.setStatus(status);
			if ("REJECTED".equals(status) && rejectionReason != null && !rejectionReason.isEmpty()) {
				refund.setRejectionReason(rejectionReason);
			}
			if ("COMPLETED".equals(status)) {
				refund.setCompletedDate(LocalDateTime.now());
				// 실제로는 환불 처리 로직 (PG 연동 등) 필요
			}
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"환불(ID: " + refundId + ") 상태가 '" + getStatusDisplayName(status) + "'(으)로 성공적으로 변경되었습니다.");
			return "redirect:/admin/refunds/" + refundId; // 상세 페이지로 리다이렉트
		} else {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "해당 환불 요청을 찾을 수 없습니다.");
			return "redirect:/admin/refunds";
		}
	}
}