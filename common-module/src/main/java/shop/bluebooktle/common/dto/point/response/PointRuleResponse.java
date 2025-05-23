package shop.bluebooktle.common.dto.point.response;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.common.domain.point.PolicyType;

public record PointRuleResponse(
	@NotNull(message = "pointSourceTypeId 값은 반드시 존재해야 합니다.")
	Long pointSourceTypeId,

	@NotNull(message = "pointPolicyId 값은 반드시 존재해야 합니다.")
	Long pointPolicyId,

	@NotNull(message = "소스 타입은 반드시 존재해야 합니다.")
	String sourceType,

	@NotNull(message = "정책 타입은 반드시 존재해야 합니다.")
	PolicyType policyType,

	@NotNull(message = "값은 반드시 존재해야 합니다.")
	@DecimalMin(value = "0.00", inclusive = false, message = "값은 0보다 커야 합니다.")
	BigDecimal value,

	boolean isActive
) {
}
