package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;

class CouponReponseTest {

	@Test
	@DisplayName("Builder로 CouponResponse 생성 테스트")
	void builderTest() {
		// given
		Long id = 1L;
		String couponName = "신규 회원 쿠폰";
		CouponTypeTarget target = CouponTypeTarget.BOOK;
		String couponTypeName = "일반 할인";
		BigDecimal minimumPayment = BigDecimal.valueOf(10000);
		LocalDateTime createdAt = LocalDateTime.now();
		String categoryName = "프로그래밍";
		String bookName = "자바의 정석";

		// when
		CouponResponse response = CouponResponse.builder()
			.id(id)
			.couponName(couponName)
			.target(target)
			.couponTypeName(couponTypeName)
			.minimumPayment(minimumPayment)
			.createdAt(createdAt)
			.categoryName(categoryName)
			.bookName(bookName)
			.build();

		// then
		assertThat(response.getId()).isEqualTo(id);
		assertThat(response.getCouponName()).isEqualTo(couponName);
		assertThat(response.getTarget()).isEqualTo(target);
		assertThat(response.getCouponTypeName()).isEqualTo(couponTypeName);
		assertThat(response.getMinimumPayment()).isEqualTo(minimumPayment);
		assertThat(response.getCreatedAt()).isEqualTo(createdAt);
		assertThat(response.getCategoryName()).isEqualTo(categoryName);
		assertThat(response.getBookName()).isEqualTo(bookName);
	}

	@Test
	@DisplayName("QueryProjection 생성자 테스트")
	void queryProjectionConstructorTest() {
		// given
		Long id = 2L;
		String couponName = "카테고리 전용 쿠폰";
		CouponTypeTarget target = CouponTypeTarget.BOOK;
		String couponTypeName = "10% 할인";
		BigDecimal minimumPayment = BigDecimal.valueOf(20000);
		LocalDateTime createdAt = LocalDateTime.of(2024, 6, 1, 12, 0);
		String categoryName = "소설";
		String bookName = null;

		// when
		CouponResponse response = new CouponResponse(
			id,
			couponName,
			target,
			couponTypeName,
			minimumPayment,
			createdAt,
			categoryName,
			bookName
		);

		// then
		assertThat(response.getId()).isEqualTo(id);
		assertThat(response.getCouponName()).isEqualTo(couponName);
		assertThat(response.getTarget()).isEqualTo(target);
		assertThat(response.getCouponTypeName()).isEqualTo(couponTypeName);
		assertThat(response.getMinimumPayment()).isEqualTo(minimumPayment);
		assertThat(response.getCreatedAt()).isEqualTo(createdAt);
		assertThat(response.getCategoryName()).isEqualTo(categoryName);
		assertThat(response.getBookName()).isNull();
	}

}
