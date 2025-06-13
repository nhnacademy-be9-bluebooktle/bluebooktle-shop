package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record BookInfoResponse(
	Long bookId,
	String title,
	List<String> authors,
	BigDecimal salePrice,
	BigDecimal price,
	String imgUrl,
	LocalDateTime createdAt,
	BigDecimal star,
	Long reviewCount,
	Long viewCount
) {
}
