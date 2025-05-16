package shop.bluebooktle.backend.book.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.backend.book.dto.request.AladinBookItem;

@Getter
@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookResponse {
	String title;
	List<AuthorResponse> authors;
	String description;
	LocalDateTime publishDate;
	String isbn;
	BigDecimal price;
	BigDecimal salePrice;
	BigDecimal salePercentage;
	String publisher;
	String categoryName;
	String imageUrl;

	public static AladinBookResponse from(AladinBookItem item) {
		BigDecimal price = BigDecimal.valueOf(item.getPriceStandard());
		BigDecimal salePrice = BigDecimal.valueOf(item.getPriceSales());

		BigDecimal salePercentage = BigDecimal.ZERO;
		if (price.compareTo(BigDecimal.ZERO) > 0) {
			salePercentage = price.subtract(salePrice)
				.multiply(BigDecimal.valueOf(100))
				.divide(price, 0, BigDecimal.ROUND_HALF_UP);
		}

		List<AuthorResponse> authorList = item.getAuthors().stream()
			.map(author -> AuthorResponse.builder()
				.authorId(author.getAuthorId())
				.authorName(author.getAuthorName())
				.build())
			.collect(Collectors.toList());

		return AladinBookResponse.builder()
			.title(item.getTitle())
			.authors(authorList)
			.description(item.getDescription())
			.publishDate(LocalDate.parse(item.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay())
			.isbn(item.getIsbn13())
			.price(price)
			.salePrice(salePrice)
			.salePercentage(salePercentage)
			.publisher(item.getPublisher())
			.categoryName(item.getCategoryName())
			.imageUrl(item.getCover())
			.build();
	}

	@Getter
	@Value
	@Builder
	public static class AuthorResponse { // 참여 작가 정보를 위한 클래스
		Integer authorId;
		String authorName;
	}

}
