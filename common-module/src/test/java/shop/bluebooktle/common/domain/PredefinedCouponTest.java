package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.coupon.PredefinedCoupon;

class PredefinedCouponTest {

	@Test
	@DisplayName("BIRTHDAY 쿠폰의 ID는 1L이어야 한다")
	void birthdayCouponIdTest() {
		assertThat(PredefinedCoupon.BIRTHDAY.getId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("WELCOME 쿠폰의 ID는 2L이어야 한다")
	void welcomeCouponIdTest() {
		assertThat(PredefinedCoupon.WELCOME.getId()).isEqualTo(2L);
	}

	@Test
	@DisplayName("Enum values()로 모든 쿠폰 타입이 조회되어야 한다")
	void enumValuesTest() {
		assertThat(PredefinedCoupon.values()).containsExactly(
			PredefinedCoupon.BIRTHDAY,
			PredefinedCoupon.WELCOME
		);
	}

}
