package shop.bluebooktle.common.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
	PENDING("결제 대기"),
	SHIPPING("배송 중"),
	COMPLETED("배송 완료"),
	RETURNED("반품"),
	CANCELED("주문 취소");

	private final String description;

	OrderStatus(String description) {
		this.description = description;
	}

}
