package shop.bluebooktle.backend.book.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class AladinBookItem {
	private String title;
	private String author;
	private String pubDate;
	private String isbn13;
	private int priceStandard;
	private int priceSales;
	private int saleRate;
	private String publisher;
	private String categoryName;
	private String cover;
}
