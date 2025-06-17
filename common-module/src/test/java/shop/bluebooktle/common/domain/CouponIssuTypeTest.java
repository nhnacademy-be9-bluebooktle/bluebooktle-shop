package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.coupon.CouponIssueType;

class CouponIssuTypeTest {

	@Test
	@DisplayName("모든 Enum 값이 정상적으로 존재해야 한다")
	void enumValuesExist() {
		assertThat(CouponIssueType.values()).containsExactly(
			CouponIssueType.BIRTHDAY,
			CouponIssueType.WELCOME,
			CouponIssueType.DIRECT
		);
	}

	@Test
	@DisplayName("valueOf() 메서드가 올바르게 동작해야 한다")
	void valueOfTest() {
		assertThat(CouponIssueType.valueOf("BIRTHDAY")).isEqualTo(CouponIssueType.BIRTHDAY);
		assertThat(CouponIssueType.valueOf("WELCOME")).isEqualTo(CouponIssueType.WELCOME);
		assertThat(CouponIssueType.valueOf("DIRECT")).isEqualTo(CouponIssueType.DIRECT);
	}

	@Test
	@DisplayName("잘못된 값으로 valueOf() 호출 시 예외가 발생해야 한다")
	void invalidValueOfTest() {
		assertThatThrownBy(() -> CouponIssueType.valueOf("INVALID"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("No enum constant");
	}

}
