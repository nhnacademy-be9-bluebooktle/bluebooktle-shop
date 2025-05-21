package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookSaleInfoUpdateResponse {
	Long id;
	String title;
	BigDecimal price;
	BigDecimal salePrice;
	BigDecimal salePercentage;
	Integer stock;
	Boolean isPackable;
	String state;

}
