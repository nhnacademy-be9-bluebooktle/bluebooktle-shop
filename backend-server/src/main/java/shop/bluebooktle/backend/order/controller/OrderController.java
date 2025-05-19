package shop.bluebooktle.backend.order.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.OrderStatus;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.entity.auth.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "주문 조회 API")
public class OrderController {

	private final OrderService orderService;

	@Operation(
		summary = "내 주문 목록 조회",
		description = """
			- **status** : 주문 상태 필터 (선택)<br>
			- **start / end** : 주문일 기간(yyyy-MM-dd'T'HH:mm:ss) (선택, 두 값 함께 전달)<br>
			- **page / size** : 페이징 파라미터(기본 0 / 10)
			"""
	)
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<Order>>> getMyOrders(
		User loginUser,
		@RequestParam(required = false) OrderStatus status,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
		@RequestParam(defaultValue = "0") @PositiveOrZero int page,
		@RequestParam(defaultValue = "10") @PositiveOrZero int size) {

		var pageable = org.springframework.data.domain.PageRequest.of(page, size);
		Page<Order> orders = orderService.getUserOrders(loginUser, status, start, end, pageable);

		PaginationData<Order> data = new PaginationData<>(orders);
		return ResponseEntity.ok(JsendResponse.success(data));
	}

	@Operation(summary = "주문 키로 단일 주문 조회 (비회원)", description = "주문 UUID로 단일 주문을 조회합니다.")
	@GetMapping("/guest/{orderKey}")
	public ResponseEntity<JsendResponse<Order>> getOrderByKey(
		@Parameter(description = "주문 UUID") @PathVariable UUID orderKey) {

		Order order = orderService.getOrderByOrderKey(orderKey);
		return ResponseEntity.ok(JsendResponse.success(order));
	}
}
