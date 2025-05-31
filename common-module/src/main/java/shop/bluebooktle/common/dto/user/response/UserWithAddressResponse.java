package shop.bluebooktle.common.dto.user.response;

import java.math.BigDecimal;
import java.util.List;

public record UserWithAddressResponse(
	Long userId,
	String name,
	String email,
	String phoneNumber,
	BigDecimal pointBalance,
	List<AddressResponse> addresses
) {
}
