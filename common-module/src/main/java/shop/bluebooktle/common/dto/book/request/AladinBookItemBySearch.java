package shop.bluebooktle.common.dto.book.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import shop.bluebooktle.backend.book.dto.response.AladinAuthorResponse;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinBookItemBySearch {
	private String title;
	private String author;
	private Long authorId;
	private List<AladinAuthorResponse> authors;
	private String description;
	private String pubDate;
	private String isbn13;
	private int priceStandard;
	private int priceSales;
	private String publisher;
	private String categoryName;
	private String cover;
}
