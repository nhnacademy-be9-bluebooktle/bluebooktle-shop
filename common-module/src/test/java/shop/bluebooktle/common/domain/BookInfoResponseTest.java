package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.book.response.BookInfoResponse;

class BookInfoResponseTest {

	@Test
	@DisplayName("BookInfoResponse 생성 및 필드 값 검증")
	void builderTest() {
		// given
		Long bookId = 1L;
		String title = "테스트 책";
		List<String> authors = List.of("홍길동", "이몽룡");
		BigDecimal salePrice = BigDecimal.valueOf(15000);
		BigDecimal price = BigDecimal.valueOf(20000);
		String imgUrl = "http://example.com/book.jpg";
		LocalDateTime createdAt = LocalDateTime.of(2024, 6, 1, 10, 30);
		BigDecimal star = BigDecimal.valueOf(4.7);
		Long reviewCount = 123L;
		Long viewCount = 456L;

		// when
		BookInfoResponse response = BookInfoResponse.builder()
			.bookId(bookId)
			.title(title)
			.authors(authors)
			.salePrice(salePrice)
			.price(price)
			.imgUrl(imgUrl)
			.createdAt(createdAt)
			.star(star)
			.reviewCount(reviewCount)
			.viewCount(viewCount)
			.build();

		// then
		assertThat(response.bookId()).isEqualTo(bookId);
		assertThat(response.title()).isEqualTo(title);
		assertThat(response.salePrice()).isEqualByComparingTo(salePrice);
		assertThat(response.price()).isEqualByComparingTo(price);
		assertThat(response.imgUrl()).isEqualTo(imgUrl);
		assertThat(response.createdAt()).isEqualTo(createdAt);
		assertThat(response.star()).isEqualByComparingTo(star);
		assertThat(response.reviewCount()).isEqualTo(reviewCount);
		assertThat(response.viewCount()).isEqualTo(viewCount);
	}

}
