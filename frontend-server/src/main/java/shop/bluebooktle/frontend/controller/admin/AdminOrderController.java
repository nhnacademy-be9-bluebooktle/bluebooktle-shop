package shop.bluebooktle.frontend.controller.admin;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Pageable;
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
import shop.bluebooktle.common.domain.order.AdminOrderSearchType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderStatusUpdateRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderTrackingNumberUpdateRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.frontend.service.AdminOrderService;

@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

	private final AdminOrderService adminOrderService;

	@GetMapping
	public String listOrders(@ModelAttribute("searchCriteria") AdminOrderSearchRequest searchRequest,
		@PageableDefault(size = 10) Pageable pageable,
		Model model, HttpServletRequest request) {
		model.addAttribute("pageTitle", "주문 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		PaginationData<AdminOrderListResponse> responseData = adminOrderService.searchOrders(searchRequest,
			pageable);

		model.addAttribute("orderPage", responseData);
		model.addAttribute("searchCriteria", searchRequest);

		model.addAttribute("orderStatusOptions", OrderStatus.values());
		model.addAttribute("paymentMethodOptions", Arrays.asList("전체", "신용카드", "가상계좌", "카카오페이", "네이버페이"));
		model.addAttribute("searchKeywordTypes", AdminOrderSearchType.values());

		return "admin/order/order_list";
	}

	@GetMapping("/{orderId}")
	public String viewOrder(@PathVariable("orderId") Long orderId, Model model, HttpServletRequest request) {
		log.info("어드민 주문 상세 페이지 요청. URI: {}, 주문 ID: {}", request.getRequestURI(), orderId);
		model.addAttribute("currentURI", request.getRequestURI());

		AdminOrderDetailResponse orderDetail = adminOrderService.getOrderDetail(orderId);

		model.addAttribute("pageTitle", "주문 상세 (번호: " + orderDetail.orderId() + ")");
		model.addAttribute("order", orderDetail);

		List<OrderStatus> updatableOrderStatuses = Arrays.stream(OrderStatus.values())
			.filter(status ->
				status != OrderStatus.RETURNED_REQUEST &&
					status != OrderStatus.RETURNED &&
					status != OrderStatus.CANCELED
			).toList();

		model.addAttribute("updatableOrderStatuses", updatableOrderStatuses);

		return "admin/order/order_detail";

	}

	@PostMapping("/{orderId}/update-status")
	public String updateOrderStatus(@PathVariable("orderId") Long orderId,
		@ModelAttribute AdminOrderStatusUpdateRequest status,
		RedirectAttributes redirectAttributes) {
		try {
			adminOrderService.updateOrderStatus(orderId, status);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"주문(ID: " + orderId + ") 상태가 '" + status.status().getDescription() + "'(으)로 성공적으로 변경되었습니다.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "주문 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/orders/" + orderId;
	}

	@PostMapping("/{orderId}/update-tracking")
	public String updateTrackingNumber(@PathVariable("orderId") Long orderId,
		@ModelAttribute AdminOrderTrackingNumberUpdateRequest status,
		RedirectAttributes redirectAttributes) {
		try {
			adminOrderService.updateOrderTrackingNumber(orderId, status);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"주문(ID: " + orderId + ")의 운송장 번호가 성공적으로 등록/수정되었습니다.");
		} catch (Exception e) {
			log.error("운송장 번호 업데이트 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "운송장 번호 업데이트 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/orders/" + orderId;
	}
}