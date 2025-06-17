package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;

class BookCartOrderResponseTest {

	@Test
	@DisplayName("BookCartOrderResponse 생성 및 필드 값 확인")
	void builderTest() {
		// given
		Long bookId = 1L;
		String title = "테스트 도서";
		BigDecimal price = BigDecimal.valueOf(20000);
		BigDecimal salePrice = BigDecimal.valueOf(15000);
		String thumbnailUrl = "http://example.com/image.jpg";
		List<String> categories = List.of("프로그래밍", "IT");
		boolean isPackable = true;
		int quantity = 3;

		// when
		BookCartOrderResponse response = BookCartOrderResponse.builder()
			.bookId(bookId)
			.title(title)
			.price(price)
			.salePrice(salePrice)
			.thumbnailUrl(thumbnailUrl)
			.categories(categories)
			.isPackable(isPackable)
			.quantity(quantity)
			.build();

		// then
		assertThat(response.bookId()).isEqualTo(bookId);
		assertThat(response.title()).isEqualTo(title);
		assertThat(response.price()).isEqualByComparingTo(price);
		assertThat(response.salePrice()).isEqualByComparingTo(salePrice);
		assertThat(response.thumbnailUrl()).isEqualTo(thumbnailUrl);
		assertThat(response.isPackable()).isTrue();
		assertThat(response.quantity()).isEqualTo(quantity);
	}

}
