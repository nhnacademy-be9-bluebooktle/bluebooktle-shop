package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;

public interface CouponService {
	PaginationData<UserCouponResponse> getAllCoupons(String filter, Pageable pageable);
}
