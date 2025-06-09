package shop.bluebooktle.backend.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;

public interface OrderService {

	Page<OrderHistoryResponse> getUserOrders(Long userId, OrderStatus status, Pageable pageable);

	OrderDetailResponse getOrderDetailByUserId(String orderKey, Long userId);

	OrderDetailResponse getOrderDetailByOrderKey(String orderKey);

	Long createOrder(OrderCreateRequest request);

	void cancelOrderMember(String orderKey, Long userId);

	void cancelOrderNonMember(String orderKey);

	void cancelOrderInternal(Long orderId);

	Page<AdminOrderListResponse> searchOrders(AdminOrderSearchRequest searchRequest, Pageable pageable);

	OrderConfirmDetailResponse getOrderByKey(String orderKey, Long userId);

	OrderConfirmDetailResponse getOrderById(Long orderId, Long userId);

	void shipOrder(Long orderId);

	AdminOrderDetailResponse getAdminOrderDetail(Long orderKey);

	void updateOrderStatus(Long orderId, OrderStatus status);

	void updateOrderTrackingNumber(Long orderId, String trackingNumber);
}
