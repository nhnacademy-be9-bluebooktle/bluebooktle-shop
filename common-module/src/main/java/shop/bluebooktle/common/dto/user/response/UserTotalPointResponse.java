package shop.bluebooktle.common.dto.user.response;

import java.math.BigDecimal;

public record UserTotalPointResponse(
	BigDecimal totalPointBalance
) {
}
