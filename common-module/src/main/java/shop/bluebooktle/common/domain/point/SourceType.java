package shop.bluebooktle.common.domain.point;

import lombok.Getter;

@Getter
public enum SourceType {
	LOGIN_EARN("로그인 적립"),
	ORDER_EARN("주문 적립"),
	REVIEW_EARN("리뷰 적립"),
	REGISTER_EARN("회원가입 적립"),
	ORDER_USE("주문 사용"),
	REFUND_EARN("환불 적립");

	public final String description;

	SourceType(String description) {
		this.description = description;
	}
}
