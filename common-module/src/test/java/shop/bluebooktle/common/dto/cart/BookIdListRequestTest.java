package shop.bluebooktle.common.dto.cart;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.cart.request.BookIdListRequest;

class BookIdListRequestTest {

	@Test
	@DisplayName("BookIdListRequest 생성 및 메서드 호출 테스트")
	void bookIdListRequest_basicTest() {
		// given
		List<Long> ids = List.of(1L, 2L, 3L);

		// when
		BookIdListRequest request = new BookIdListRequest(ids);

		// then
		assertThat(request.bookIds()).isEqualTo(ids);
		assertThat(request.toString()).contains("1", "2", "3");

		// equals & hashCode
		BookIdListRequest same = new BookIdListRequest(List.of(1L, 2L, 3L));
		BookIdListRequest different = new BookIdListRequest(List.of(4L, 5L));

		assertThat(request).isEqualTo(same);
		assertThat(request).hasSameHashCodeAs(same);
		assertThat(request).isNotEqualTo(different);
	}

}
