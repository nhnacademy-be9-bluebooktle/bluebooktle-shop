package shop.bluebooktle.backend.book.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

@Getter
@Builder
@AllArgsConstructor
public class BookSaleInfoResponse {
	Long id;
	String title;
	BigDecimal price;
	BigDecimal salePrice;
	Integer stock;
	Boolean isPackable;
	String state;
	Long viewCount;
	Long searchCount;

	public static BookSaleInfoResponse fromEntity(BookSaleInfo entity) {
		return BookSaleInfoResponse.builder()
			.id(entity.getId())
			.title(entity.getBook().getTitle())
			.price(entity.getPrice())
			.salePrice(entity.getSalePrice())
			.stock(entity.getStock())
			.isPackable(entity.isPackable())
			.state(entity.getState().name())
			.viewCount(entity.getViewCount())
			.searchCount(entity.getSearchCount())
			.build();
	}

}
