package shop.bluebooktle.common.dto.user.response;

import java.math.BigDecimal;

public record UserOrderResponse(
	Long userId,
	String name,
	String email,
	String phoneNumber,
	BigDecimal pointBalance,
	Long addressId,
	String alias,
	String roadAddress,
	String detailAddress,
	String postalCode
) {
}
