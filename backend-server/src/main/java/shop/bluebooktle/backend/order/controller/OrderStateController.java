package shop.bluebooktle.backend.order.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class OrderStateController {

	private final OrderStateService orderStateService;

	// 전체 조회
	@GetMapping("/admin/order-status/all")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<List<OrderStateResponse>>> getAllStatus() {
		List<OrderStateResponse> states = orderStateService.getAll().stream()
			.map(OrderStateResponse::from)
			.collect(Collectors.toList());
		return ResponseEntity.ok(JsendResponse.success(states));
	}

	// 상태명으로 조회
	@GetMapping("/admin/order-status")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<OrderStateResponse>> getStatus(@RequestParam OrderStatus name) {
		Optional<OrderStateResponse> state = orderStateService.getByState(name);
		return state.map(JsendResponse::success)
			.map(ResponseEntity::ok)
			.orElseThrow(() -> new OrderStateNotFoundException(name.toString()));
	}

}
