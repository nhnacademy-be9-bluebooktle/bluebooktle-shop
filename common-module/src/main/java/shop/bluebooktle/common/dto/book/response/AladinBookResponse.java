package shop.bluebooktle.common.dto.book.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import shop.bluebooktle.common.dto.book.request.AladinBookItem;

@Getter
@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookResponse {
	String title;
	String author;
	String description;
	LocalDateTime publishDate;
	String isbn;
	BigDecimal price;
	BigDecimal salePrice;
	BigDecimal salePercentage;
	String publisher;
	String categoryName;
	String imgUrl;

	public static AladinBookResponse from(AladinBookItem item) {
		BigDecimal price = BigDecimal.valueOf(item.getPriceStandard());
		BigDecimal salePrice = BigDecimal.valueOf(item.getPriceSales());

		BigDecimal salePercentage = BigDecimal.ZERO;
		if (price.compareTo(BigDecimal.ZERO) > 0) {
			salePercentage = price.subtract(salePrice)
				.multiply(BigDecimal.valueOf(100))
				.divide(price, 0, BigDecimal.ROUND_HALF_UP);
		}

		return AladinBookResponse.builder()
			.title(item.getTitle())
			.author(item.getAuthor())
			.description(item.getDescription())
			.publishDate(LocalDate.parse(item.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay())
			.isbn(item.getIsbn13())
			.price(price)
			.salePrice(salePrice)
			.salePercentage(salePercentage)
			.publisher(item.getPublisher())
			.categoryName(item.getCategoryName())
			.imgUrl(item.getCover())
			.build();

	}
}
