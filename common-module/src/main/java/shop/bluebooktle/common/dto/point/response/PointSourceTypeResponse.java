package shop.bluebooktle.common.dto.point.response;

import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.common.domain.point.ActionType;

public record PointSourceTypeResponse(
	@NotNull(message = "Id값은 Null이 아닙니다.")
	Long id,
	@NotNull(message = "동작 유형 타입은 Null이 아닙니다.")
	ActionType actionType,
	@NotNull(message = "소스 타입은 Null이 아닙니다.")
	String sourceType
) {
}
