package shop.bluebooktle.common.domain.payment;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentStatusTest {

	@Test
	@DisplayName("모든 PaymentStatus enum 값이 올바르게 정의되어 있어야 한다")
	void valuesTest() {
		assertThat(PaymentStatus.values()).containsExactly(
			PaymentStatus.READY,
			PaymentStatus.IN_PROGRESS,
			PaymentStatus.WAITING_FOR_DEPOSIT,
			PaymentStatus.DONE,
			PaymentStatus.CANCELED,
			PaymentStatus.PARTIAL_CANCELED,
			PaymentStatus.ABORTED,
			PaymentStatus.EXPIRED
		);
	}

	@Test
	@DisplayName("각 PaymentStatus의 description 값이 정확해야 한다")
	void descriptionTest() {
		assertThat(PaymentStatus.READY.getDescription()).isEqualTo("준비");
		assertThat(PaymentStatus.IN_PROGRESS.getDescription()).isEqualTo("진행중");
		assertThat(PaymentStatus.WAITING_FOR_DEPOSIT.getDescription()).isEqualTo("입금대기");
		assertThat(PaymentStatus.DONE.getDescription()).isEqualTo("결제완료");
		assertThat(PaymentStatus.CANCELED.getDescription()).isEqualTo("결제취소");
		assertThat(PaymentStatus.PARTIAL_CANCELED.getDescription()).isEqualTo("부분취소");
		assertThat(PaymentStatus.ABORTED.getDescription()).isEqualTo("결제승인실패");
		assertThat(PaymentStatus.EXPIRED.getDescription()).isEqualTo("결제유효시간만료");
	}

	@Test
	@DisplayName("valueOf로 enum을 조회할 수 있어야 한다")
	void valueOfTest() {
		assertThat(PaymentStatus.valueOf("READY")).isEqualTo(PaymentStatus.READY);
		assertThat(PaymentStatus.valueOf("DONE")).isEqualTo(PaymentStatus.DONE);
	}

}
