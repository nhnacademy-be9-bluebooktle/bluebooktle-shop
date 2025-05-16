package shop.bluebooktle.backend.book.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

@Getter
@Builder
@AllArgsConstructor
public class BookSaleInfoRegisterResponse {
	Long id;
	String title;
	BigDecimal price;
	BigDecimal salePrice;
	BigDecimal salePercentage;
	Integer stock;
	Boolean isPackable;
	String state;

	public static BookSaleInfoRegisterResponse fromEntity(BookSaleInfo entity) {
		return BookSaleInfoRegisterResponse.builder()
			.id(entity.getId())
			.title(entity.getBook().getTitle())
			.price(entity.getPrice())
			.salePrice(entity.getSalePrice())
			.stock(entity.getStock())
			.isPackable(entity.isPackable())
			.salePercentage(entity.getSalePercentage())
			.state(entity.getState().name())
			.build();
	}
}
