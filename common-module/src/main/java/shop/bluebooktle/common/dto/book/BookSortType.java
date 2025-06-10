package shop.bluebooktle.common.dto.book;

import lombok.Getter;

@Getter
public enum BookSortType {
	POPULARITY("인기도순"),
	NEWEST("신상품순"),
	PRICE_ASC("최저가"),
	PRICE_DESC("최고가"),
	RATING("평점순"),
	REVIEW_COUNT("리뷰순");

	private final String displayName;

	BookSortType(String displayName) {
		this.displayName = displayName;
	}

}
