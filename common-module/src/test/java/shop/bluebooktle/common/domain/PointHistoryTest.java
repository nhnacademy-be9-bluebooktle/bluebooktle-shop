package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;

class PointHistoryTest {

	@Test
	@DisplayName("Builder를 통한 PointHistory 생성 테스트")
	void builderTest() {
		// given
		PointSourceTypeEnum sourceType = PointSourceTypeEnum.SIGNUP_EARN;
		User user = User.builder().id(1L).build();  // 단위 테스트용 mock user
		BigDecimal value = BigDecimal.valueOf(1000);

		// when
		PointHistory pointHistory = PointHistory.builder()
			.sourceType(sourceType)
			.user(user)
			.value(value)
			.build();

		// then
		assertThat(pointHistory.getSourceType()).isEqualTo(PointSourceTypeEnum.SIGNUP_EARN);
		assertThat(pointHistory.getUser()).isEqualTo(user);
		assertThat(pointHistory.getValue()).isEqualByComparingTo("1000");
	}

	@Test
	@DisplayName("equals/hashCode 테스트 (id 기준)")
	void equalsHashCodeTest() throws Exception {
		// given
		User user = User.builder().id(1L).build();
		PointHistory one = PointHistory.builder()
			.sourceType(PointSourceTypeEnum.SIGNUP_EARN)
			.user(user)
			.value(BigDecimal.valueOf(1000))
			.build();

		PointHistory two = PointHistory.builder()
			.sourceType(PointSourceTypeEnum.SIGNUP_EARN)
			.user(user)
			.value(BigDecimal.valueOf(1000))
			.build();

		setId(one, 1L);
		setId(two, 1L);

		// then
		assertThat(one).isEqualTo(two);
		assertThat(one).hasSameHashCodeAs(two);
	}

	private void setId(PointHistory obj, Long id) throws Exception {
		var field = PointHistory.class.getDeclaredField("id");
		field.setAccessible(true);
		field.set(obj, id);
	}
}
