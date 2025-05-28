package shop.bluebooktle.common.dto.point.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PointAdjustmentRequest(
	@NotNull(message = "포인트 출처 ID는 필수입니다.")
	Long pointSourceTypeId,

	@NotNull(message = "포인트 값은 필수입니다.")
	@DecimalMin(value = "0.01", inclusive = true, message = "포인트는 최소 0.01 이상이어야 합니다.")
	BigDecimal value
) {
}