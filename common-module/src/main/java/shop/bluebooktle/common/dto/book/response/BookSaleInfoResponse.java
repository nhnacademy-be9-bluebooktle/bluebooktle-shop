package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;

@Getter
@Builder
@AllArgsConstructor
public class BookSaleInfoResponse {
	Long id;
	String title;
	BigDecimal price;
	BigDecimal salePrice;
	BigDecimal salePercentage;
	Integer stock;
	Boolean isPackable;
	BookSaleInfoState state;
}
