package shop.bluebooktle.common.dto.point.request;

import jakarta.validation.constraints.NotNull;
import shop.bluebooktle.common.domain.point.ActionType;

public record PointSourceTypeCreateRequest(
	@NotNull(message = "actionType은 필수입니다.")
	ActionType actionType,

	@NotNull(message = "sourceType은 필수입니다.")
	String sourceType
) {
}
