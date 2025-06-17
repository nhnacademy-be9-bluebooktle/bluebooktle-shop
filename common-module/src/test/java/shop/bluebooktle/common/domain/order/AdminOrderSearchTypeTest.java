package shop.bluebooktle.common.domain.order;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdminOrderSearchTypeTest {

	@Test
	@DisplayName("Enum values()는 모든 검색 타입을 포함해야 한다")
	void valuesTest() {
		assertThat(AdminOrderSearchType.values()).containsExactly(
			AdminOrderSearchType.ORDER_KEY,
			AdminOrderSearchType.ORDERER_NAME,
			AdminOrderSearchType.ORDERER_LOGIN_ID,
			AdminOrderSearchType.RECEIVER_NAME,
			AdminOrderSearchType.PRODUCT_NAME
		);
	}

	@Test
	@DisplayName("각 enum의 description 값이 올바르게 매핑되어야 한다")
	void descriptionTest() {
		assertThat(AdminOrderSearchType.ORDER_KEY.getDescription()).isEqualTo("주문번호");
		assertThat(AdminOrderSearchType.ORDERER_NAME.getDescription()).isEqualTo("주문자명");
		assertThat(AdminOrderSearchType.ORDERER_LOGIN_ID.getDescription()).isEqualTo("주문자 ID");
		assertThat(AdminOrderSearchType.RECEIVER_NAME.getDescription()).isEqualTo("수령인명");
		assertThat(AdminOrderSearchType.PRODUCT_NAME.getDescription()).isEqualTo("상품명");
	}

	@Test
	@DisplayName("valueOf()로 enum 값을 가져올 수 있어야 한다")
	void valueOfTest() {
		assertThat(AdminOrderSearchType.valueOf("ORDER_KEY")).isEqualTo(AdminOrderSearchType.ORDER_KEY);
	}

}
