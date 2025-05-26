package shop.bluebooktle.common.dto.point.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointHistoryResponse(
	Long id,
	BigDecimal value,
	PointSourceTypeResponse pointSourceType,
	LocalDateTime createdAt
) {
}