package shop.bluebooktle.backend.coupon.dto;

import lombok.Getter;
import lombok.Setter;
import shop.bluebooktle.common.domain.CouponTypeTarget;

@Getter
@Setter
public class CouponSearchRequest {
	CouponTypeTarget target;
	Long bookId;
	Long categoryId;
}
