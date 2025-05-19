package shop.bluebooktle.backend.book.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookItem {
	private String title;
	private String author;
	private String description;
	private String pubDate;
	private String isbn13;
	private int priceStandard;
	private int priceSales;
	private String publisher;
	private String categoryName;
	private String cover;
}
