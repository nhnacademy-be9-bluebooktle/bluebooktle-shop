package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.domain.point.PolicyType;
import shop.bluebooktle.common.entity.point.PointPolicy;
import shop.bluebooktle.common.entity.point.PointSourceType;

class PointPolicyTest {

	@Test
	@DisplayName("Builder를 사용한 PointPolicy 생성 테스트")
	void builderTest() {
		PointSourceType sourceType = PointSourceType.builder()
			.actionType(ActionType.EARN)
			.sourceType("회원가입")
			.build();

		PointPolicy policy = PointPolicy.builder()
			.pointSourceType(sourceType)
			.policyType(PolicyType.AMOUNT)
			.value(BigDecimal.valueOf(1000))
			.build();

		assertThat(policy.getPointSourceType()).isEqualTo(sourceType);
		assertThat(policy.getPolicyType()).isEqualTo(PolicyType.AMOUNT);
		assertThat(policy.getValue()).isEqualByComparingTo("1000");
		assertThat(policy.getIsActive()).isFalse(); // 기본값
	}

	@Test
	@DisplayName("changeIsActive() 테스트")
	void changeIsActiveTest() {
		PointPolicy policy = createDefaultPolicy();
		policy.changeIsActive(true);
		assertThat(policy.getIsActive()).isTrue();
	}

	@Test
	@DisplayName("changeValue() 테스트")
	void changeValueTest() {
		PointPolicy policy = createDefaultPolicy();
		policy.changeValue(BigDecimal.valueOf(3000));
		assertThat(policy.getValue()).isEqualByComparingTo("3000");
	}

	@Test
	@DisplayName("changePolicyType() 테스트")
	void changePolicyTypeTest() {
		PointPolicy policy = createDefaultPolicy();
		policy.changePolicyType(PolicyType.PERCENTAGE);
		assertThat(policy.getPolicyType()).isEqualTo(PolicyType.PERCENTAGE);
	}

	private PointPolicy createDefaultPolicy() {
		PointSourceType sourceType = PointSourceType.builder()
			.actionType(ActionType.EARN)
			.sourceType("출석체크")
			.build();

		return PointPolicy.builder()
			.pointSourceType(sourceType)
			.policyType(PolicyType.AMOUNT)
			.value(BigDecimal.valueOf(1000))
			.build();
	}

}
