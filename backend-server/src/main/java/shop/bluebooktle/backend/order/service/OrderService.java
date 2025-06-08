package shop.bluebooktle.backend.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;

public interface OrderService {

	Page<OrderHistoryResponse> getUserOrders(Long userId, OrderStatus status, Pageable pageable);

	Order getOrderByOrderKey(String orderKey);

	OrderDetailResponse getOrderDetailByUserId(String orderKey, Long userId);

	OrderDetailResponse getOrderDetailByOrdererPhoneNumber(String orderKey, String phoneNumber);

	OrderDetailResponse getOrderDetailInternal(String orderKey);

	OrderConfirmDetailResponse getOrderDetailsForConfirmation(Long orderId, Long userId);

	Long createOrder(OrderCreateRequest request);

	void cancelOrder(String orderKey, Long userId);

	void cancelOrderInternal(Long orderId);

	Page<AdminOrderListResponse> searchOrders(AdminOrderSearchRequest searchRequest, Pageable pageable);

	OrderConfirmDetailResponse getOrderByKey(String orderKey, Long userId);

	OrderConfirmDetailResponse getOrderById(Long orderId, Long userId);

	void shipOrder(Long orderId);
}
