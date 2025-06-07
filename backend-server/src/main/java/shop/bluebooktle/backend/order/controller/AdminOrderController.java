package shop.bluebooktle.backend.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

	private final OrderService orderService;

	@PostMapping("/{orderId}/ship")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> shipOrder(@PathVariable Long orderId) {
		orderService.shipOrder(orderId);
		return ResponseEntity.status(HttpStatus.OK)
			.body(JsendResponse.success(null));
	}
}