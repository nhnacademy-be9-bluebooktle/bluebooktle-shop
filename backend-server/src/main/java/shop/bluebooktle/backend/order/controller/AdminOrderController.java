package shop.bluebooktle.backend.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
		Page<AdminOrderListResponse> page = orderService.searchOrders(searchRequest, pageable);
		PaginationData<AdminOrderListResponse> paginationData = new PaginationData<>(page);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}
}