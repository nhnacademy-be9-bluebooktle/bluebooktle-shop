package shop.bluebooktle.backend.order.repository;

import java.util.Optional;

import shop.bluebooktle.backend.order.entity.Order;

public interface OrderQueryRepository {

	Optional<Order> findFullOrderDetailsByIdAndUserId(Long orderId, Long userId);

}