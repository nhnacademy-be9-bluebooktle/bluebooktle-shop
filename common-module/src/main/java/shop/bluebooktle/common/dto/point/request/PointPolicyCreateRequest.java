package shop.bluebooktle.common.dto.point.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.common.domain.point.PolicyType;

public record PointPolicyCreateRequest(
	@NotNull(message = "pointSourceTypeId는 필수입니다.")
	Long pointSourceTypeId,

	@NotNull(message = "policyType은 필수입니다.")
	PolicyType policyType,

	@NotNull(message = "value는 필수입니다.")
	@DecimalMin(value = "0.00", inclusive = false, message = "value는 0보다 커야 합니다.")
	BigDecimal value
) {
}