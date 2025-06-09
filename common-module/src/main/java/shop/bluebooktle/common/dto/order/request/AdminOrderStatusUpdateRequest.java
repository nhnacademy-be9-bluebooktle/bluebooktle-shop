package shop.bluebooktle.common.dto.order.request;

import shop.bluebooktle.common.domain.order.OrderStatus;

public record AdminOrderStatusUpdateRequest(
	OrderStatus status
) {
}
