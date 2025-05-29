package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;

@Builder
public record BookCartOrderResponse(Long id, String title, BigDecimal price, BigDecimal salePrice, Integer stock,
									BigDecimal salePercentage, String thumbnailUrl, List<String> categories,
									BookSaleInfoState bookSaleInfoState, int quantity) {
}
