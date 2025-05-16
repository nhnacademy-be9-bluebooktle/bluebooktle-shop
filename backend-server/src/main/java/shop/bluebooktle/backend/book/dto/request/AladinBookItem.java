package shop.bluebooktle.backend.book.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookItem {
	private String title;
	private List<AuthorInfo> authors;
	private String description;
	private String pubDate;
	private String isbn13;
	private int priceStandard;
	private int priceSales;
	private String publisher;
	private String categoryName;
	private String cover;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AuthorInfo {
		private Integer authorId;
		private String authorName;
	}

}
