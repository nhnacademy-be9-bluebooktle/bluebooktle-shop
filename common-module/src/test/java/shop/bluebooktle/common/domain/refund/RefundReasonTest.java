package shop.bluebooktle.common.domain.refund;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefundReasonTest {

	@Test
	@DisplayName("모든 RefundReason enum 값이 올바르게 정의되어 있어야 한다")
	void valuesTest() {
		assertThat(RefundReason.values()).containsExactly(
			RefundReason.CHANGE_OF_MIND,
			RefundReason.DEFECT,
			RefundReason.DAMAGED,
			RefundReason.WRONG_DELIVERY,
			RefundReason.OTHER
		);
	}

	@Test
	@DisplayName("각 RefundReason의 description 값이 정확해야 한다")
	void descriptionTest() {
		assertThat(RefundReason.CHANGE_OF_MIND.getDescription()).isEqualTo("단순 변심");
		assertThat(RefundReason.DEFECT.getDescription()).isEqualTo("파손");
		assertThat(RefundReason.DAMAGED.getDescription()).isEqualTo("파본");
		assertThat(RefundReason.WRONG_DELIVERY.getDescription()).isEqualTo("오배송");
		assertThat(RefundReason.OTHER.getDescription()).isEqualTo("기타");
	}

	@Test
	@DisplayName("valueOf로 enum을 조회할 수 있어야 한다")
	void valueOfTest() {
		assertThat(RefundReason.valueOf("DEFECT")).isEqualTo(RefundReason.DEFECT);
	}

}
