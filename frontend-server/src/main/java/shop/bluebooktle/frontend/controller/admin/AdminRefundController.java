package shop.bluebooktle.frontend.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.refund.RefundSearchType;
import shop.bluebooktle.common.domain.refund.RefundStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.common.dto.refund.response.RefundListResponse;
import shop.bluebooktle.frontend.service.AdminRefundService;

@Slf4j
@Controller
@RequestMapping("/admin/refunds")
@RequiredArgsConstructor
public class AdminRefundController {

	private final AdminRefundService adminRefundService;

	@GetMapping
	public String listRefunds(@ModelAttribute("searchRequest") RefundSearchRequest searchRequest,
		@PageableDefault(size = 10, sort = "requestDate", direction = Sort.Direction.DESC) Pageable pageable,
		Model model, HttpServletRequest request) {
		model.addAttribute("pageTitle", "환불 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		PaginationData<RefundListResponse> paginationData = adminRefundService.getRefunds(searchRequest, pageable);

		model.addAttribute("paginationData", paginationData);
		model.addAttribute("searchRequest", searchRequest);

		model.addAttribute("refundStatusOptions", RefundStatus.values());
		model.addAttribute("searchTypeOptions", RefundSearchType.values());

		return "admin/refund/refund_list";
	}

	@GetMapping("/{refundId}")
	public String refundDetailPage(@PathVariable Long refundId, Model model, HttpServletRequest request,
		RedirectAttributes redirectAttributes) {
		try {
			AdminRefundDetailResponse refundDetail = adminRefundService.getRefundDetail(refundId);
			model.addAttribute("pageTitle", "환불 상세 (ID: " + refundId + ")");
			model.addAttribute("currentURI", request.getRequestURI());
			model.addAttribute("refund", refundDetail);
			model.addAttribute("nextStatusOptions", RefundStatus.values());
			model.addAttribute("updateRequest", new RefundUpdateRequest(refundId, null));
			return "admin/refund/refund_detail";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "환불 정보를 불러오는 데 실패했습니다.");
			return "redirect:/admin/refunds";
		}
	}

	@PostMapping("/update-status")
	public String updateRefundStatus(@ModelAttribute RefundUpdateRequest request,
		RedirectAttributes redirectAttributes) {
		log.info("환불 상태 변경 요청: {}", request);

		try {
			adminRefundService.updateRefund(request);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"환불(ID: " + request.refundId() + ") 상태가 성공적으로 변경되었습니다.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "상태 변경 중 오류가 발생했습니다: " + e.getMessage());
		}

		return "redirect:/admin/refunds/" + request.refundId();
	}
}