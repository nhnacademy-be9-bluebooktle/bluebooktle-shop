package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;

@Getter
@Builder
@Value
@AllArgsConstructor
public class BookCartOrderResponse {
	Long id;
	String title;
	BigDecimal price;
	BigDecimal salePrice;
	Integer stock;
	BigDecimal salePercentage;
	String thumbnailUrl;
	List<String> categories;
	BookSaleInfoState bookSaleInfoState;
}
