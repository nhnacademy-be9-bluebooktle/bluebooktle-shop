package shop.bluebooktle.common.dto.cart;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.cart.request.CartItemRequest;

class CartItemRequestTest {

	@Test
	@DisplayName("CartItemRequest 생성 및 메서드 테스트")
	void cartItemRequest_basicTest() {
		// given
		Long bookId = 10L;
		int quantity = 3;

		// when
		CartItemRequest request = new CartItemRequest(bookId, quantity);

		// then
		assertThat(request.bookId()).isEqualTo(bookId);
		assertThat(request.quantity()).isEqualTo(quantity);
		assertThat(request.toString()).contains("10", "3");

		// equals & hashCode
		CartItemRequest same = new CartItemRequest(10L, 3);
		CartItemRequest different = new CartItemRequest(20L, 1);

		assertThat(request).isEqualTo(same);
		assertThat(request).hasSameHashCodeAs(same);
		assertThat(request).isNotEqualTo(different);
	}
}