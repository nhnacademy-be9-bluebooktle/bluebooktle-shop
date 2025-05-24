package shop.bluebooktle.backend.order.dto.response;

import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.common.domain.order.OrderStatus;

public record OrderStateResponse(
	Long id,
	OrderStatus state
) {
	public static OrderStateResponse from(OrderState orderState) {
		return new OrderStateResponse(
			orderState.getId(),
			orderState.getState()
		);
	}
}
