package shop.bluebooktle.frontend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponTypeRegisterFormDto {
	@NotBlank
	private String name;

	@NotBlank
	private String targetType;

	@NotNull
	private BigDecimal minimumPayment;

	private BigDecimal discountPrice;
	private BigDecimal maximumDiscountPrice;
	private Integer discountPercent;

	// 변환 메서드 (백엔드 DTO로 변환)
	public CouponTypeRegisterRequest toRequest() {
		return CouponTypeRegisterRequest.builder()
			.name(this.name)
			.target(CouponTypeTarget.valueOf(this.targetType))
			.minimumPayment(this.minimumPayment)
			.discountPrice(this.discountPrice)
			.maximumDiscountPrice(this.maximumDiscountPrice)
			.discountPercent(this.discountPercent)
			.build();
	}
}
