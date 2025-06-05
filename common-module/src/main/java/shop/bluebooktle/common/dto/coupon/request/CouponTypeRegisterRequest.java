package shop.bluebooktle.common.dto.coupon.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponTypeRegisterRequest {

	@NotBlank(message = "쿠폰 정책 이름은 필수입니다.")
	@Size(max = 100, message = "쿠폰 정책명은 100자 이하로 입력해주세요.")
	String name;
	@NotNull(message = "적용 가능 범위는 필수입니다.")
	CouponTypeTarget target;
	@NotNull(message = "최소 주문 금액은 필수입니다.")
	@DecimalMin(value = "0.0", inclusive = false, message = "최소 주문 금액은 0보다 커야 합니다.")
	BigDecimal minimumPayment;
	//절댓값
	BigDecimal discountPrice;
	//상댓값
	BigDecimal maximumDiscountPrice;
	Integer discountPercent;
}
