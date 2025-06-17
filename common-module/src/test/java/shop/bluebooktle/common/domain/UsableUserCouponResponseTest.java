package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponResponse;

class UsableUserCouponResponseTest {

	@Test
	@DisplayName("QueryProjection 생성자 테스트")
	void queryProjectionConstructorTest() {
		// given
		Long userCouponId = 1L;
		Long couponId = 2L;
		String couponName = "신규회원쿠폰";
		String couponTypeName = "정액할인";
		BigDecimal minimumPayment = BigDecimal.valueOf(10000);
		BigDecimal discountPrice = BigDecimal.valueOf(2000);
		BigDecimal maximumDiscountPrice = BigDecimal.valueOf(5000);
		Integer discountPercent = 10;
		String bookName = "자바의 정석";
		String categoryName = "프로그래밍";

		// when
		UsableUserCouponResponse response = new UsableUserCouponResponse(
			userCouponId, couponId, couponName, couponTypeName,
			minimumPayment, discountPrice, maximumDiscountPrice, discountPercent,
			bookName, categoryName
		);

		// then
		assertThat(response.getUserCouponId()).isEqualTo(userCouponId);
		assertThat(response.getCouponId()).isEqualTo(couponId);
		assertThat(response.getCouponName()).isEqualTo(couponName);
		assertThat(response.getCouponTypeName()).isEqualTo(couponTypeName);
		assertThat(response.getMinimumPayment()).isEqualByComparingTo(minimumPayment);
		assertThat(response.getDiscountPrice()).isEqualByComparingTo(discountPrice);
		assertThat(response.getMaximumDiscountPrice()).isEqualByComparingTo(maximumDiscountPrice);
		assertThat(response.getDiscountPercent()).isEqualTo(discountPercent);
		assertThat(response.getBookName()).isEqualTo(bookName);
		assertThat(response.getCategoryName()).isEqualTo(categoryName);
	}

	@Test
	@DisplayName("Builder를 통한 객체 생성 테스트")
	void builderTest() {
		UsableUserCouponResponse response = UsableUserCouponResponse.builder()
			.userCouponId(11L)
			.couponId(22L)
			.couponName("10% 할인쿠폰")
			.couponTypeName("비율할인")
			.minimumPayment(BigDecimal.valueOf(15000))
			.discountPrice(BigDecimal.valueOf(0)) // 비율 할인일 경우 0
			.maximumDiscountPrice(BigDecimal.valueOf(3000))
			.discountPercent(10)
			.bookName(null)
			.categoryName("소설")
			.build();

		assertThat(response.getUserCouponId()).isEqualTo(11L);
		assertThat(response.getCouponId()).isEqualTo(22L);
		assertThat(response.getCouponName()).isEqualTo("10% 할인쿠폰");
		assertThat(response.getDiscountPercent()).isEqualTo(10);
		assertThat(response.getBookName()).isNull();
		assertThat(response.getCategoryName()).isEqualTo("소설");
	}

	@Test
	@DisplayName("기본 생성자 테스트")
	void noArgsConstructorTest() {
		UsableUserCouponResponse response = new UsableUserCouponResponse();
		assertThat(response).isNotNull();
	}

}
