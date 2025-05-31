package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;

@Builder
public record BookCartOrderResponse(
	Long bookId,
	String title,
	BigDecimal price,
	BigDecimal salePrice,
	String thumbnailUrl,
	List<String> categories,
	int quantity
) {
}