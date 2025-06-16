package shop.bluebooktle.backend.order.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.dto.response.OrderStateResponse;
import shop.bluebooktle.backend.order.service.OrderStateService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.exception.order.order_state.OrderStateNotFoundException;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "관리자 주문 상태 API", description = "주문 상태를 조회합니다.")
public class OrderStateController {

	private final OrderStateService orderStateService;

	@Operation(summary = "모든 주문 상태 조회", description = "관리자 페이지에서 모든 주문 상태를 조회합니다.")
	@GetMapping("/admin/order-status/all")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<List<OrderStateResponse>>> getAllStatus() {
		List<OrderStateResponse> states = orderStateService.getAll().stream()
			.map(OrderStateResponse::from)
			.collect(Collectors.toList());
		return ResponseEntity.ok(JsendResponse.success(states));
	}

	@Operation(summary = "주문 상태 조회", description = "관리자 페이지에서 주문 상태명에 따른 주문 상태를 조회합니다.")
	@GetMapping("/admin/order-status")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<OrderStateResponse>> getStatus(@RequestParam OrderStatus name) {
		Optional<OrderStateResponse> state = orderStateService.getByState(name);
		return state.map(JsendResponse::success)
			.map(ResponseEntity::ok)
			.orElseThrow(() -> new OrderStateNotFoundException(name.toString()));
	}

}
