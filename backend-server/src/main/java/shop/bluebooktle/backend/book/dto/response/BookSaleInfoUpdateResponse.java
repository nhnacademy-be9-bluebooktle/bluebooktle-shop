package shop.bluebooktle.backend.book.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

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

	public static BookSaleInfoUpdateResponse fromEntity(BookSaleInfo entity) {
		return BookSaleInfoUpdateResponse.builder()
			.id(entity.getId())
			.title(entity.getBook().getTitle())
			.price(entity.getPrice())
			.salePrice(entity.getSalePrice())
			.salePercentage(entity.getSalePercentage())
			.stock(entity.getStock())
			.isPackable(entity.isPackable())
			.state(entity.getState().name())
			.build();
	}
}
