package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;

class CouponTypeResponseTest {

	@Test
	@DisplayName("QueryProjection 생성자 테스트")
	void queryProjectionConstructorTest() {
		// given
		Long id = 1L;
		String name = "신규회원 할인";
		CouponTypeTarget target = CouponTypeTarget.BOOK;
		BigDecimal minimumPayment = BigDecimal.valueOf(10000);
		BigDecimal discountPrice = BigDecimal.valueOf(2000);
		BigDecimal maximumDiscountPrice = BigDecimal.valueOf(5000);
		Integer discountPercent = 10;

		// when
		CouponTypeResponse response = new CouponTypeResponse(
			id,
			name,
			target,
			minimumPayment,
			discountPrice,
			maximumDiscountPrice,
			discountPercent
		);

		// then
		assertThat(response.getId()).isEqualTo(id);
		assertThat(response.getName()).isEqualTo(name);
		assertThat(response.getTarget()).isEqualTo(target);
		assertThat(response.getMinimumPayment()).isEqualByComparingTo(minimumPayment);
		assertThat(response.getDiscountPrice()).isEqualByComparingTo(discountPrice);
		assertThat(response.getMaximumDiscountPrice()).isEqualByComparingTo(maximumDiscountPrice);
		assertThat(response.getDiscountPercent()).isEqualTo(discountPercent);
	}

	@Test
	@DisplayName("NoArgsConstructor + Setter 테스트 (필드 접근 불가로 테스트 생략 가능)")
	void noArgsConstructorTest() {
		// 일반적으로 QueryDSL용 DTO라면 setter는 없어야 하므로, 테스트 생략 가능
		// 단, Jackson 직렬화 등을 위해 기본 생성자는 존재
		CouponTypeResponse response = new CouponTypeResponse();
		assertThat(response).isNotNull();
	}

}
