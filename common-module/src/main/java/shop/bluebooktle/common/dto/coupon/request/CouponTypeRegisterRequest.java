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

	@NotBlank
	String name;
	@NotNull
	CouponTypeTarget target;
	@NotNull
	BigDecimal minimumPayment;
	//절댓값
	BigDecimal discountPrice;
	//상댓값
	BigDecimal maximumDiscountPrice;
	Integer discountPercent;
}
