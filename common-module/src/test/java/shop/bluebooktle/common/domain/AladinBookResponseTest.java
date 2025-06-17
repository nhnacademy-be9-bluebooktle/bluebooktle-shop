package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.book.request.AladinBookItem;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;

class AladinBookResponseTest {

	@Test
	@DisplayName("AladinBookItem → AladinBookResponse 변환 테스트")
	void from_aladinBookItem_success() {
		// given
		AladinBookItem item = AladinBookItem.builder()
			.title("테스트 도서")
			.author("홍길동")
			.description("이것은 테스트 도서입니다.")
			.pubDate("2023-12-25")
			.isbn13("9781234567890")
			.priceStandard(20000)
			.priceSales(15000)
			.publisher("테스트출판사")
			.categoryName("프로그래밍")
			.cover("http://example.com/image.jpg")
			.build();

		// when
		AladinBookResponse response = AladinBookResponse.from(item);

		// then
		assertThat(response.getTitle()).isEqualTo("테스트 도서");
		assertThat(response.getAuthor()).isEqualTo("홍길동");
		assertThat(response.getDescription()).isEqualTo("이것은 테스트 도서입니다.");
		assertThat(response.getPublishDate()).isEqualTo(LocalDateTime.of(2023, 12, 25, 0, 0));
		assertThat(response.getIsbn()).isEqualTo("9781234567890");
		assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));
		assertThat(response.getSalePrice()).isEqualByComparingTo(BigDecimal.valueOf(15000));
		assertThat(response.getSalePercentage()).isEqualByComparingTo(
			BigDecimal.valueOf(25)); // (20000 - 15000) / 20000 * 100
		assertThat(response.getPublisher()).isEqualTo("테스트출판사");
		assertThat(response.getCategoryName()).isEqualTo("프로그래밍");
		assertThat(response.getImgUrl()).isEqualTo("http://example.com/image.jpg");
	}

	@Test
	@DisplayName("할인 없는 경우 salePercentage 0% 테스트")
	void from_zeroDiscount() {
		AladinBookItem item = AladinBookItem.builder()
			.title("정가 도서")
			.author("작가")
			.description("정가 판매")
			.pubDate("2024-01-01")
			.isbn13("9781111111111")
			.priceStandard(15000)
			.priceSales(15000)
			.publisher("출판사")
			.categoryName("카테고리")
			.cover("http://example.com/cover.jpg")
			.build();

		AladinBookResponse response = AladinBookResponse.from(item);

		assertThat(response.getSalePercentage()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("정가가 0원인 경우 할인율 계산 예외 방지 테스트")
	void from_zeroPriceStandard() {
		AladinBookItem item = AladinBookItem.builder()
			.title("무료 도서")
			.author("저자")
			.description("무료 배포")
			.pubDate("2024-01-01")
			.isbn13("0000000000000")
			.priceStandard(0)
			.priceSales(0)
			.publisher("출판사")
			.categoryName("카테고리")
			.cover("http://example.com/free.jpg")
			.build();

		AladinBookResponse response = AladinBookResponse.from(item);

		assertThat(response.getSalePercentage()).isEqualByComparingTo(BigDecimal.ZERO);
	}

}
