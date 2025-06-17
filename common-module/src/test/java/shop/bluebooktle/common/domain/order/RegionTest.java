package shop.bluebooktle.common.domain.order;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegionTest {

	@Test
	@DisplayName("Region enum 값이 모두 정의되어 있어야 한다")
	void valuesTest() {
		assertThat(Region.values()).containsExactly(
			Region.ALL,
			Region.JEJU,
			Region.MOUNTAINOUS_AREA
		);
	}

	@Test
	@DisplayName("Region enum을 valueOf로 조회할 수 있어야 한다")
	void valueOfTest() {
		assertThat(Region.valueOf("JEJU")).isEqualTo(Region.JEJU);
		assertThat(Region.valueOf("ALL")).isEqualTo(Region.ALL);
		assertThat(Region.valueOf("MOUNTAINOUS_AREA")).isEqualTo(Region.MOUNTAINOUS_AREA);
	}

}
