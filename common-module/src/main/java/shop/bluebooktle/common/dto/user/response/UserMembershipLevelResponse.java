package shop.bluebooktle.common.dto.user.response;

import java.math.BigDecimal;

public record UserMembershipLevelResponse(
	Long userId,
	BigDecimal netAmount,
	Long membershipLevelId,
	String membershipLevelName
) {
}
