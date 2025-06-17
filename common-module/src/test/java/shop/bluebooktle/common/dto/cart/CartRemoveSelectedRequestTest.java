package shop.bluebooktle.common.dto.cart;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.cart.request.CartRemoveSelectedRequest;

class CartRemoveSelectedRequestTest {

	@Test
	@DisplayName("CartRemoveSelectedRequest 생성 및 메서드 테스트")
	void cartRemoveSelectedRequest_basicTest() {
		// given
		String id = "guest-abc";
		List<Long> bookIds = List.of(1L, 2L, 3L);

		// when
		CartRemoveSelectedRequest request = new CartRemoveSelectedRequest(id, bookIds);

		// then
		assertThat(request.id()).isEqualTo(id);
		assertThat(request.bookIds()).isEqualTo(bookIds);
		assertThat(request.toString()).contains("guest-abc", "1", "2", "3");

		// equals & hashCode
		CartRemoveSelectedRequest same = new CartRemoveSelectedRequest("guest-abc", List.of(1L, 2L, 3L));
		CartRemoveSelectedRequest different = new CartRemoveSelectedRequest("guest-xyz", List.of(4L, 5L));

		assertThat(request).isEqualTo(same);
		assertThat(request).hasSameHashCodeAs(same);
		assertThat(request).isNotEqualTo(different);
	}
}