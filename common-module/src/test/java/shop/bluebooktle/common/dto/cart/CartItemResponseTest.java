package shop.bluebooktle.common.dto.cart;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.cart.response.CartItemResponse;

class CartItemResponseTest {

	@Test
	@DisplayName("CartItemResponse 생성 및 메서드 테스트")
	void cartItemResponse_basicTest() {
		// given
		Long bookId = 101L;
		int quantity = 5;

		// when
		CartItemResponse response = new CartItemResponse(bookId, quantity);

		// then
		assertThat(response.bookId()).isEqualTo(bookId);
		assertThat(response.quantity()).isEqualTo(quantity);
		assertThat(response.toString()).contains("101", "5");

		// equals & hashCode
		CartItemResponse same = new CartItemResponse(101L, 5);
		CartItemResponse different = new CartItemResponse(202L, 2);

		assertThat(response).isEqualTo(same)
			.hasSameHashCodeAs(same)
			.isNotEqualTo(different);
	}
}