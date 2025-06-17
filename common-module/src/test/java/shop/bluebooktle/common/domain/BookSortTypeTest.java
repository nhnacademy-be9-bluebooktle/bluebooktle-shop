package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.book.BookSortType;

class BookSortTypeTest {

	@Test
	@DisplayName("BookSortType displayName 매핑 확인")
	void displayNameTest() {
		assertThat(BookSortType.POPULARITY.getDisplayName()).isEqualTo("인기도순");
		assertThat(BookSortType.NEWEST.getDisplayName()).isEqualTo("신상품순");
		assertThat(BookSortType.PRICE_ASC.getDisplayName()).isEqualTo("최저가");
		assertThat(BookSortType.PRICE_DESC.getDisplayName()).isEqualTo("최고가");
		assertThat(BookSortType.RATING.getDisplayName()).isEqualTo("평점순");
		assertThat(BookSortType.REVIEW_COUNT.getDisplayName()).isEqualTo("리뷰순");
	}

	@Test
	@DisplayName("BookSortType enum 값 수 검증")
	void enumCountTest() {
		assertThat(BookSortType.values()).hasSize(6);
	}

}
