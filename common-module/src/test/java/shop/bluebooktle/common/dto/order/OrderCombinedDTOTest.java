package shop.bluebooktle.common.dto.order;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.order.request.AdminOrderStatusUpdateRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderTrackingNumberUpdateRequest;

class OrderCombinedDTOTest {

	@Test
	@DisplayName("AdminOrderStatusUpdateRequest 생성 및 메서드 테스트")
	void adminOrderStatusUpdateRequest_basicTest() {
		// given
		OrderStatus status = OrderStatus.SHIPPING;

		// when
		AdminOrderStatusUpdateRequest request = new AdminOrderStatusUpdateRequest(status);

		// then
		assertThat(request.status()).isEqualTo(status);
		assertThat(request.toString()).contains("SHIPPING");

		// equals & hashCode
		AdminOrderStatusUpdateRequest same = new AdminOrderStatusUpdateRequest(OrderStatus.SHIPPING);
		AdminOrderStatusUpdateRequest different = new AdminOrderStatusUpdateRequest(OrderStatus.CANCELED);

		assertThat(request)
			.isEqualTo(same)
			.hasSameHashCodeAs(same)
			.isNotEqualTo(different);
	}

	@Test
	@DisplayName("AdminOrderTrackingNumberUpdateRequest 생성 및 메서드 테스트")
	void adminOrderTrackingNumberUpdateRequest_basicTest() {
		// given
		String trackingNumber = "TRACK123456";

		// when
		AdminOrderTrackingNumberUpdateRequest request = new AdminOrderTrackingNumberUpdateRequest(trackingNumber);

		// then
		assertThat(request.trackingNumber()).isEqualTo(trackingNumber);
		assertThat(request.toString()).contains("TRACK123456");

		// equals & hashCode
		AdminOrderTrackingNumberUpdateRequest same = new AdminOrderTrackingNumberUpdateRequest("TRACK123456");
		AdminOrderTrackingNumberUpdateRequest different = new AdminOrderTrackingNumberUpdateRequest("DIFF987654");

		assertThat(request)
			.isEqualTo(same)
			.hasSameHashCodeAs(same)
			.isNotEqualTo(different);
	}

}