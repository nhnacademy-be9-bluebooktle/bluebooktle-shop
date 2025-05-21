package shop.bluebooktle.common.dto.point.response;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.common.domain.point.PolicyType;

public record PointPolicyResponse(
	@NotNull(message = "Id값은 Null이 아닙니다.")
	Long id,

	@NotNull(message = "정책 타입은 Null이 아닙니다.")
	PolicyType policyType,

	@NotNull(message = "값은 Null이 아닙니다.")
	BigDecimal value,
	
	boolean isActive
) {
}
