package shop.bluebooktle.common.dto.point.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import shop.bluebooktle.common.domain.point.PolicyType;

public record PointPolicyUpdateRequest(

	Long pointPolicyId,

	PolicyType policyType,

	@DecimalMin(value = "0.00", inclusive = false, message = "값은 0보다 커야 합니다.")
	BigDecimal value,

	Boolean isActive
) {
}
