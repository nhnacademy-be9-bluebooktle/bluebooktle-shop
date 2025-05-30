package shop.bluebooktle.common.dto.book.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;

// 테스트 편의를 위해 @Builder 추가

@Getter
@Builder
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
