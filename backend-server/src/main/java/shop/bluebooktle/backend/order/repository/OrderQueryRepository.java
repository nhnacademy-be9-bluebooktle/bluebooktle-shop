package shop.bluebooktle.backend.order.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;

public interface OrderQueryRepository {
	Optional<Order> findFullOrderDetailsById(Long orderId);

	Optional<Order> findOrderDetailsByOrderKey(String orderKey);

	Page<Order> searchOrders(AdminOrderSearchRequest searchRequest, Pageable pageable);

	BigDecimal findTotalPackagingPriceByOrderId(Long orderId);

	Optional<Order> findAdminOrderDetailsByOrderId(Long orderId);

	Optional<Order> findOrderForRefund(Long orderId);

}