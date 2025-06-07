package shop.bluebooktle.backend.book_order.service;

import java.util.List;

import shop.bluebooktle.common.dto.book_order.request.UserCouponBookOrderRequest;

public interface UserCouponBookOrderService {
	void saveCouponUsage(UserCouponBookOrderRequest request);

	void deleteCouponUsage(Long orderId, List<Long> userCouponIds);
}
