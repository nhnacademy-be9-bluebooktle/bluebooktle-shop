package shop.bluebooktle.common.dto.coupon;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalculatedDiscountDetails {
	private String policyTypeDescription;
	private BigDecimal originalDiscountValue;
	private BigDecimal maxDiscountAmountForPercentage;
	private BigDecimal appliedDiscountAmount;
}