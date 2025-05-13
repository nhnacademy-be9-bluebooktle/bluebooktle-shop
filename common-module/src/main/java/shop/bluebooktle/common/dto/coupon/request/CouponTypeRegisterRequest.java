package shop.bluebooktle.common.dto.coupon.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import shop.bluebooktle.common.domain.CouponTypeTarget;

@Value
@Builder
public class CouponTypeRegisterRequest {

	@NotBlank(message = "쿠폰 정책 이름은 필수입니다.")
	String name;
	@NotNull(message = "적용 가능 범위는 필수입니다.")
	CouponTypeTarget target;
	@NotNull(message = "최소 주문 금액은 필수입니다.")
	BigDecimal minimumPayment;
	//절댓값
	BigDecimal discountPrice;
	//상댓값
	BigDecimal maximumDiscountPrice;
	Integer discountPercent;
}
