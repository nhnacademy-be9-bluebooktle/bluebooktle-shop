package shop.bluebooktle.common.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminOrderSearchType {
	ORDER_KEY("주문번호"),
	ORDERER_NAME("주문자명"),
	ORDERER_LOGIN_ID("주문자 ID"),
	RECEIVER_NAME("수령인명"),
	PRODUCT_NAME("상품명");

	private final String description;
}