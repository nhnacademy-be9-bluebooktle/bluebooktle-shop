package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.book.BookSaleInfoState;

class BookSaleInfoStateTest {

	@Test
	@DisplayName("BookSaleInfoState enum 값 확인")
	void enumValuesTest() {
		assertThat(BookSaleInfoState.valueOf("AVAILABLE")).isEqualTo(BookSaleInfoState.AVAILABLE);
		assertThat(BookSaleInfoState.valueOf("LOW_STOCK")).isEqualTo(BookSaleInfoState.LOW_STOCK);
		assertThat(BookSaleInfoState.valueOf("SALE_ENDED")).isEqualTo(BookSaleInfoState.SALE_ENDED);
		assertThat(BookSaleInfoState.valueOf("DELETED")).isEqualTo(BookSaleInfoState.DELETED);
	}

	@Test
	@DisplayName("BookSaleInfoState enum 전체 개수 확인")
	void enumSizeTest() {
		assertThat(BookSaleInfoState.values()).hasSize(4);
	}

}
