package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.coupon.CouponIssueStatus;

class CouponIssueStatusTest {

	@Test
	@DisplayName("모든 Enum 값이 정상적으로 존재해야 한다")
	void enumValuesExist() {
		assertThat(CouponIssueStatus.values()).containsExactly(
			CouponIssueStatus.WAITING,
			CouponIssueStatus.RETRIED,
			CouponIssueStatus.SUCCESS,
			CouponIssueStatus.FAILED
		);
	}

	@Test
	@DisplayName("valueOf() 메서드가 올바르게 동작해야 한다")
	void valueOfTest() {
		assertThat(CouponIssueStatus.valueOf("WAITING")).isEqualTo(CouponIssueStatus.WAITING);
		assertThat(CouponIssueStatus.valueOf("RETRIED")).isEqualTo(CouponIssueStatus.RETRIED);
		assertThat(CouponIssueStatus.valueOf("SUCCESS")).isEqualTo(CouponIssueStatus.SUCCESS);
		assertThat(CouponIssueStatus.valueOf("FAILED")).isEqualTo(CouponIssueStatus.FAILED);
	}

	@Test
	@DisplayName("잘못된 값으로 valueOf() 호출 시 예외가 발생해야 한다")
	void invalidValueOfTest() {
		assertThatThrownBy(() -> CouponIssueStatus.valueOf("INVALID"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("No enum constant");
	}

}
