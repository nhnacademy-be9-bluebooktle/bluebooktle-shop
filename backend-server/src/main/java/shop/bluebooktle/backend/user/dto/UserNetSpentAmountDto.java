package shop.bluebooktle.backend.user.dto;

import java.math.BigDecimal;

public record UserNetSpentAmountDto(
	Long userId,
	BigDecimal netAmount) {
}
