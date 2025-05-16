package shop.bluebooktle.backend.order.service;

import java.util.List;
import java.util.Optional;

import shop.bluebooktle.backend.order.dto.response.OrderStateResponse;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.common.domain.OrderStatus;

public interface OrderStateService {

	List<OrderState> getAll();

	Optional<OrderStateResponse> getByState(OrderStatus state);

}
