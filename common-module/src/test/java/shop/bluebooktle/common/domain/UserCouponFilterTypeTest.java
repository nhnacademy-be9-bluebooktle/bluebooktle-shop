package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;

class UserCouponFilterTypeTest {

	@Test
	@DisplayName("Enum values()는 모든 필터 타입을 포함해야 한다")
	void valuesTest() {
		assertThat(UserCouponFilterType.values()).containsExactly(
			UserCouponFilterType.ALL,
			UserCouponFilterType.USABLE,
			UserCouponFilterType.USED,
			UserCouponFilterType.EXPIRED
		);
	}

	@Test
	@DisplayName("valueOf()는 문자열로 Enum을 반환해야 한다")
	void valueOfTest() {
		assertThat(UserCouponFilterType.valueOf("ALL")).isEqualTo(UserCouponFilterType.ALL);
		assertThat(UserCouponFilterType.valueOf("USABLE")).isEqualTo(UserCouponFilterType.USABLE);
		assertThat(UserCouponFilterType.valueOf("USED")).isEqualTo(UserCouponFilterType.USED);
		assertThat(UserCouponFilterType.valueOf("EXPIRED")).isEqualTo(UserCouponFilterType.EXPIRED);
	}

}
