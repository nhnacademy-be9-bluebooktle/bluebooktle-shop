package shop.bluebooktle.common.domain.refund;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefundStatusTest {

	@Test
	@DisplayName("RefundStatus enum 값이 정확히 정의되어 있어야 한다")
	void valuesTest() {
		assertThat(RefundStatus.values()).containsExactly(
			RefundStatus.PENDING,
			RefundStatus.COMPLETE,
			RefundStatus.REJECTED
		);
	}

	@Test
	@DisplayName("description 필드가 올바르게 설정되어 있어야 한다")
	void descriptionTest() {
		assertThat(RefundStatus.PENDING.getDescription()).isEqualTo("처리 대기");
		assertThat(RefundStatus.COMPLETE.getDescription()).isEqualTo("환불 완료");
		assertThat(RefundStatus.REJECTED.getDescription()).isEqualTo("환불 거절");
	}

	@Test
	@DisplayName("valueOf로 enum 값을 조회할 수 있어야 한다")
	void valueOfTest() {
		assertThat(RefundStatus.valueOf("PENDING")).isEqualTo(RefundStatus.PENDING);
		assertThat(RefundStatus.valueOf("COMPLETE")).isEqualTo(RefundStatus.COMPLETE);
		assertThat(RefundStatus.valueOf("REJECTED")).isEqualTo(RefundStatus.REJECTED);
	}

}
