package shop.bluebooktle.common.dto.membership;

import java.math.BigDecimal;

public record MembershipLevelDetailDto(
	Long id,
	String name,
	Integer rate,
	BigDecimal minNetSpent,
	BigDecimal maxNetSpent
) {
}
