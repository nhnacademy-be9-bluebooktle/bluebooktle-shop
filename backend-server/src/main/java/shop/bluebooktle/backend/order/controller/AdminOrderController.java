package shop.bluebooktle.backend.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderStatusUpdateRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderTrackingNumberUpdateRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.common.security.Auth;

@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Tag(name = "관리자 주문 API", description = "관리자용 주문 조회 및 관리 API")
public class AdminOrderController {

	private final OrderService orderService;

	@Operation(summary = "관리자 주문 목록 조회", description = "주문 목록을 검색하고 조회합니다.")
	@GetMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PaginationData<AdminOrderListResponse>>> searchOrders(
		@Parameter(description = "주문 검색 조건") @ModelAttribute AdminOrderSearchRequest searchRequest,
		@PageableDefault(size = 10) Pageable pageable
	) {
		log.info("관리자 주문 목록 조회");
		Page<AdminOrderListResponse> page = orderService.searchOrders(searchRequest, pageable);
		PaginationData<AdminOrderListResponse> paginationData = new PaginationData<>(page);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(summary = "관리자 주문 상세 조회", description = "관리자 주문 상세 조회합니다.")
	@GetMapping("/{orderId}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<AdminOrderDetailResponse>> getAdminOrderDetail(
		@PathVariable("orderId") Long orderId
	) {
		log.info("관리자 주문 상세 조회");
		AdminOrderDetailResponse response = orderService.getAdminOrderDetail(orderId);
		return ResponseEntity.ok(JsendResponse.success(response));
	}

	@PostMapping("/{orderId}/update-status")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> updateOrderStatus(
		@PathVariable("orderId") Long orderId,
		@RequestBody AdminOrderStatusUpdateRequest status
	) {
		log.info("{} 주문 상태 변경", orderId);
		orderService.updateOrderStatus(orderId, status.status());
		return ResponseEntity.status(HttpStatus.OK)
			.body(JsendResponse.success(null));
	}

	@PostMapping("/{orderId}/update-tracking")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> updateTrackingNumber(
		@PathVariable("orderId") Long orderId,
		@RequestBody AdminOrderTrackingNumberUpdateRequest trackNumber
	) {
		orderService.updateOrderTrackingNumber(orderId, trackNumber.trackingNumber());
		return ResponseEntity.status(HttpStatus.OK)
			.body(JsendResponse.success(null));
	}

}