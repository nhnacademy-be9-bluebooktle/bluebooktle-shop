package shop.bluebooktle.backend.coupon.dto;

import lombok.Getter;
import lombok.Setter;
import shop.bluebooktle.common.domain.CouponTypeTarget;

@Getter
@Setter
public class CouponSearchRequest {
	CouponTypeTarget target;
	Boolean bookRelated; // 도서 관련
	Boolean categoryId; // 카테고리 관련
	Boolean active; // 현재 사용 가능
}
