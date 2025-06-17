package shop.bluebooktle.common.dto.cart;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.cart.request.CartRemoveOneRequest;

class CartRemoveOneRequestTest {

	@Test
	@DisplayName("CartRemoveOneRequest 생성 및 메서드 테스트")
	void cartRemoveOneRequest_basicTest() {
		// given
		String id = "guest-123";
		Long bookId = 42L;

		// when
		CartRemoveOneRequest request = new CartRemoveOneRequest(id, bookId);

		// then
		assertThat(request.id()).isEqualTo(id);
		assertThat(request.bookId()).isEqualTo(bookId);
		assertThat(request.toString()).contains("guest-123", "42");

		// equals & hashCode
		CartRemoveOneRequest same = new CartRemoveOneRequest("guest-123", 42L);
		CartRemoveOneRequest different = new CartRemoveOneRequest("guest-456", 99L);

		assertThat(request).isEqualTo(same);
		assertThat(request).hasSameHashCodeAs(same);
		assertThat(request).isNotEqualTo(different);
	}
}