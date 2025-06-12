package shop.bluebooktle.common.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
	PENDING("대기"),
	PREPARING("상품 준비중"),
	SHIPPING("배송 중"),
	COMPLETED("배송 완료"),
	RETURNED("반품완료"),
	CANCELED("주문 취소"),
	RETURNED_REQUEST("반품 요청");

	private final String description;

	OrderStatus(String description) {
		this.description = description;
	}

}
