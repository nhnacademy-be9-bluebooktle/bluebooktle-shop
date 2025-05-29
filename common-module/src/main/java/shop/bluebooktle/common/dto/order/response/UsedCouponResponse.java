package shop.bluebooktle.common.dto.order.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsedCouponResponse {
	private String couponName;
	private String target;
	private BigDecimal discountAmount;
}