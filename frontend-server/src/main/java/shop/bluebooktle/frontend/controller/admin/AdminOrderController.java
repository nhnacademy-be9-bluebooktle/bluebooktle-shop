package shop.bluebooktle.frontend.controller.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.order.AdminOrderSearchType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.frontend.service.AdminOrderService;

@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

	private final AdminOrderService adminOrderService;

	@Getter
	@Setter
	@ToString
	static class OrderDetailDto {
		// 주문 기본 정보
		private Long orderId;
		private String orderNumber;
		private LocalDateTime orderDate;
		// 주문자 정보 (별도 DTO 또는 필드들)
		private String ordererName;
		private String ordererLoginId;
		private String ordererPhoneNumber;
		private String ordererEmail;
		// 수령인 정보
		private String recipientName;
		private String recipientPhoneNumber;
		// 배송지 정보
		private String shippingAddressPostcode;
		private String shippingAddressBase;
		private String shippingAddressDetail;
		private String shippingMemo;

		private List<OrderedProductDto> orderedProducts;
		private PaymentDetailDto paymentInfo;
		private List<String> packagingOptions; // 간소화된 예시
		private DeliveryInfoDto deliveryInfo;
		private String currentOrderStatus;

		// 임시 생성자 및 데이터 채우기
		public OrderDetailDto(Long orderId, String orderNumber) {
			this.orderId = orderId;
			this.orderNumber = orderNumber;
			this.orderDate = LocalDateTime.now().minusDays(2);
			this.ordererName = "김블루";
			this.ordererLoginId = "blueuser";
			this.ordererPhoneNumber = "010-1234-5678";
			this.ordererEmail = "blue@example.com";
			this.recipientName = "이북클";
			this.recipientPhoneNumber = "010-9876-5432";
			this.shippingAddressPostcode = "12345";
			this.shippingAddressBase = "서울시 강남구 테헤란로";
			this.shippingAddressDetail = "블루빌딩 101호";
			this.shippingMemo = "문 앞에 놓아주세요.";
			this.orderedProducts = Arrays.asList(
				new OrderedProductDto("코딩의 정석", 2, 25000L),
				new OrderedProductDto("스프링 부트 마스터", 1, 30000L)
			);
			this.paymentInfo = new PaymentDetailDto("신용카드", 80000L, 3000L, 1000L, 500L, 81500L);
			this.packagingOptions = Arrays.asList("선물 포장 (레드)", "쇼핑백 추가");
			this.deliveryInfo = new DeliveryInfoDto("배송준비중", null, null);
			this.currentOrderStatus = "결제완료";
		}
	}

	@Getter
	@Setter
	@ToString
	static class OrderedProductDto {
		private String productName;
		private int quantity;
		private Long priceAtOrder; // 주문 당시 개당 가격
		private Long totalPrice;

		public OrderedProductDto(String productName, int quantity, Long priceAtOrder) {
			this.productName = productName;
			this.quantity = quantity;
			this.priceAtOrder = priceAtOrder;
			this.totalPrice = priceAtOrder * quantity;
		}
	}

	@Getter
	@Setter
	@ToString
	static class PaymentDetailDto {
		private String paymentMethod;
		private Long totalProductAmount;
		private Long shippingFee;
		private Long usedPoints;
		private Long couponDiscountAmount;
		private Long finalPaymentAmount;

		public PaymentDetailDto(String pm, Long tpa, Long sf, Long up, Long cda, Long fpa) {
			this.paymentMethod = pm;
			this.totalProductAmount = tpa;
			this.shippingFee = sf;
			this.usedPoints = up;
			this.couponDiscountAmount = cda;
			this.finalPaymentAmount = fpa;
		}
	}

	@Getter
	@Setter
	@ToString
	static class DeliveryInfoDto {
		private String deliveryStatus;
		private String trackingNumber;
		private String deliveryCompany;

		public DeliveryInfoDto(String ds, String tn, String dc) {
			this.deliveryStatus = ds;
			this.trackingNumber = tn;
			this.deliveryCompany = dc;
		}
	}

	@Getter
	@Setter
	static class OrderSearchCriteria { // 실제로는 OrderSearchCriteriaDto 등으로 명명
		private String searchKeywordType;
		private String searchKeyword;
		private String orderStatusFilter;
		private LocalDate startDate;
		private LocalDate endDate;
		private String paymentMethodFilter;
		private int page = 1;
		private int size = 10;
	}

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

		// TODO: adminOrderService.getOrderDetail(orderKey) 호출
		// 임시 데이터
		OrderDetailDto orderDetail = new OrderDetailDto(orderId,
			"ORD" + LocalDate.now().getYear() + String.format("%02d", LocalDate.now().getMonthValue()) + String.format(
				"%02d", LocalDate.now().getDayOfMonth()) + String.format("%03d", orderId % 1000));

		model.addAttribute("pageTitle", "주문 상세 (번호: " + orderDetail.getOrderNumber() + ")");
		model.addAttribute("order", orderDetail);

		// 주문 상태 변경을 위한 옵션
		model.addAttribute("updatableOrderStatuses", Arrays.asList("배송준비중", "배송중", "배송완료", "주문취소")); // 현재 상태에 따라 다르게 제공

		return "admin/order/order_detail";
	}

	@PostMapping("/{orderId}/update-status")
	public String updateOrderStatus(@PathVariable("orderId") Long orderId,
		@RequestParam("status") String status,
		RedirectAttributes redirectAttributes) {
		log.info("주문 상태 변경 요청. 주문 ID: {}, 변경할 상태: {}", orderId, status);
		try {
			// TODO: adminOrderService.updateOrderStatus(orderKey, status);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"주문(ID: " + orderId + ") 상태가 '" + status + "'(으)로 성공적으로 변경되었습니다.");
		} catch (Exception e) {
			log.error("주문 상태 변경 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "주문 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/orders/" + orderId;
	}

	@PostMapping("/{orderId}/update-tracking")
	public String updateTrackingNumber(@PathVariable("orderId") Long orderId,
		@RequestParam("trackingNumber") String trackingNumber,
		// @RequestParam("deliveryCompany") String deliveryCompany, // 필요시 배송업체도 함께
		RedirectAttributes redirectAttributes) {
		try {
			// TODO: adminOrderService.updateTrackingNumber(orderKey, trackingNumber, deliveryCompany);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"주문(ID: " + orderId + ")의 운송장 번호가 성공적으로 등록/수정되었습니다.");
		} catch (Exception e) {
			log.error("운송장 번호 업데이트 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "운송장 번호 업데이트 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/orders/" + orderId;
	}
}