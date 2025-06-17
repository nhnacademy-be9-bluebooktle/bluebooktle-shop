package shop.bluebooktle.common.domain.order;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderStatusTest {

	@Test
	@DisplayName("OrderStatus enum 값이 모두 정의되어 있어야 한다")
	void valuesTest() {
		assertThat(OrderStatus.values()).containsExactly(
			OrderStatus.PENDING,
			OrderStatus.PREPARING,
			OrderStatus.SHIPPING,
			OrderStatus.COMPLETED,
			OrderStatus.RETURNED,
			OrderStatus.CANCELED,
			OrderStatus.RETURNED_REQUEST
		);
	}

	@Test
	@DisplayName("OrderStatus의 description이 올바르게 매핑되어야 한다")
	void descriptionTest() {
		assertThat(OrderStatus.PENDING.getDescription()).isEqualTo("대기");
		assertThat(OrderStatus.PREPARING.getDescription()).isEqualTo("상품 준비중");
		assertThat(OrderStatus.SHIPPING.getDescription()).isEqualTo("배송 중");
		assertThat(OrderStatus.COMPLETED.getDescription()).isEqualTo("배송 완료");
		assertThat(OrderStatus.RETURNED.getDescription()).isEqualTo("반품완료");
		assertThat(OrderStatus.CANCELED.getDescription()).isEqualTo("주문 취소");
		assertThat(OrderStatus.RETURNED_REQUEST.getDescription()).isEqualTo("반품 요청");
	}

	@Test
	@DisplayName("OrderStatus enum을 valueOf로 조회할 수 있어야 한다")
	void valueOfTest() {
		assertThat(OrderStatus.valueOf("SHIPPING")).isEqualTo(OrderStatus.SHIPPING);
	}

}
