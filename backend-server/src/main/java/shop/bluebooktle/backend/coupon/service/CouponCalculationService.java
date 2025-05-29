package shop.bluebooktle.backend.coupon.service;

import shop.bluebooktle.backend.book_order.entity.UserCouponBookOrder;
import shop.bluebooktle.common.dto.coupon.CalculatedDiscountDetails;

public interface CouponCalculationService {
	CalculatedDiscountDetails calculateDiscountDetails(UserCouponBookOrder appliedCouponInfo);
}