package shop.bluebooktle.backend.order.repository;

import java.util.List;
import java.util.Optional;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.dto.order.response.OrderItemResponse;
import shop.bluebooktle.common.dto.order.response.UsedCouponResponse;

public interface OrderQueryRepository {

	Optional<Order> findOrderBaseByIdAndUserId(Long orderId, Long userId);

	List<OrderItemResponse> findOrderItemsDtoByOrderId(Long orderId);

	List<UsedCouponResponse> findUsedCouponsDtoByOrderId(Long orderId);

}