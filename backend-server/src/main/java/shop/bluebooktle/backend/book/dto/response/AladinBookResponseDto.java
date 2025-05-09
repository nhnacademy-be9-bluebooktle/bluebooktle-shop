package shop.bluebooktle.backend.book.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.backend.book.dto.AladinBookItem;

@Getter
@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookResponseDto {
	String title;
	String author;
	String description;
	LocalDateTime publishDate;
	String isbn;
	BigDecimal price;
	BigDecimal salePrice;
	BigDecimal salePercentage; // 할인율 나중에 책 등록할때 계산해서 넣어야할지?
	String publisher;
	String categoryName;
	String imageUrl;

	public static AladinBookResponseDto from(AladinBookItem item) {
		return AladinBookResponseDto.builder()
			.title(item.getTitle())
			.author(item.getAuthor())
			.description(item.getDescription())
			.publishDate(LocalDate.parse(item.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay())
			.isbn(item.getIsbn13())
			.price(BigDecimal.valueOf(item.getPriceStandard()))
			.salePrice(BigDecimal.valueOf(item.getPriceSales()))
			.salePercentage(BigDecimal.valueOf(item.getSaleRate()))
			.publisher(item.getPublisher())
			.categoryName(item.getCategoryName())
			.imageUrl(item.getCover())
			.build();
	}
}
