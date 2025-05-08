package shop.bluebooktle.backend.book.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import lombok.Getter;

@Data
public class AladinBookDto {
	private String title;
	private String author;
	private LocalDateTime publishDate;
	private String isbn;
	private BigDecimal price;
	private BigDecimal salePrice;
	private BigDecimal salePercentage;
	private String publisher;
	private String categoryName;
	private String imageUrl;

	public static AladinBookDto from(AladinBookItem aladinBookItem) {
		LocalDateTime publishDate = LocalDate.parse(aladinBookItem.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();

		return new AladinBookDto(
			aladinBookItem.getTitle(),
			aladinBookItem.getAuthor(),
			publishDate,
			aladinBookItem.getIsbn13(),
			BigDecimal.valueOf(aladinBookItem.getPriceStandard()),
			BigDecimal.valueOf(aladinBookItem.getPriceSales()),
			BigDecimal.valueOf(aladinBookItem.getSaleRate()),
			aladinBookItem.getPublisher(),
			aladinBookItem.getCategoryName(),
			aladinBookItem.getCover());
	}

	public AladinBookDto(String title, String author, LocalDateTime publishDate, String isbn, BigDecimal price,
		BigDecimal salePrice, BigDecimal salePercentage, String publisher, String categoryName, String imageUrl) {
		this.title = title;
		this.author = author;
		this.publishDate = publishDate;
		this.isbn = isbn;
		this.price = price;
		this.salePrice = salePrice;
		this.salePercentage = salePercentage;
		this.publisher = publisher;
		this.categoryName = categoryName;
		this.imageUrl = imageUrl;
	}
}
