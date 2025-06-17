package shop.bluebooktle.common.domain.refund;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefundSearchTypeTest {

	@Test
	@DisplayName("RefundSearchType enum 값이 정확히 정의되어 있어야 한다")
	void valuesTest() {
		assertThat(RefundSearchType.values()).containsExactly(
			RefundSearchType.ORDER_KEY,
			RefundSearchType.ORDERER_NAME
		);
	}

	@Test
	@DisplayName("valueOf로 enum 값을 조회할 수 있어야 한다")
	void valueOfTest() {
		assertThat(RefundSearchType.valueOf("ORDER_KEY")).isEqualTo(RefundSearchType.ORDER_KEY);
		assertThat(RefundSearchType.valueOf("ORDERER_NAME")).isEqualTo(RefundSearchType.ORDERER_NAME);
	}

}
