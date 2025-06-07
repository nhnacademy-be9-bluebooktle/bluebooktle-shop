package shop.bluebooktle.frontend.service;

import java.util.List;

import shop.bluebooktle.common.dto.book_order.request.UserCouponBookOrderRequest;

public interface UserCouponUsageService {
	void saveCouponUsage(UserCouponBookOrderRequest request);

	void deleteCouponUsage(Long orderId, List<Long> userCouponIds);
}
