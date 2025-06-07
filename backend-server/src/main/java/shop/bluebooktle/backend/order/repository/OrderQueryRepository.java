package shop.bluebooktle.backend.order.repository;

import java.math.BigDecimal;
import java.util.Optional;

import shop.bluebooktle.backend.order.entity.Order;

public interface OrderQueryRepository {
	Optional<Order> findFullOrderDetailsById(Long orderId);

	BigDecimal findTotalPackagingPriceByOrderId(Long orderId);

}