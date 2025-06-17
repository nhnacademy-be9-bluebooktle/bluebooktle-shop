package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.entity.point.PointSourceType;

class PointSourceTypeTest {

	@Test
	@DisplayName("Builder를 통한 PointSourceType 생성 테스트")
	void builderTest() {
		// given
		ActionType actionType = ActionType.EARN;
		String sourceType = "회원가입";

		// when
		PointSourceType pointSourceType = PointSourceType.builder()
			.actionType(actionType)
			.sourceType(sourceType)
			.build();

		// then
		assertThat(pointSourceType.getActionType()).isEqualTo(ActionType.EARN);
		assertThat(pointSourceType.getSourceType()).isEqualTo("회원가입");
	}

	@Test
	@DisplayName("equals, hashCode 테스트 (ID 기반)")
	void equalsAndHashCodeTest() {
		PointSourceType one = PointSourceType.builder()
			.actionType(ActionType.EARN)
			.sourceType("가입보너스")
			.build();

		PointSourceType two = PointSourceType.builder()
			.actionType(ActionType.EARN)
			.sourceType("가입보너스")
			.build();

		// ID를 수동으로 설정 (테스트 목적상)
		setId(one, 1L);
		setId(two, 1L);

		assertThat(one).isEqualTo(two);
		assertThat(one.hashCode()).isEqualTo(two.hashCode());
	}

	private void setId(PointSourceType obj, Long id) {
		try {
			var field = PointSourceType.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(obj, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
