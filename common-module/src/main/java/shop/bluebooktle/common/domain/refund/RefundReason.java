package shop.bluebooktle.common.domain.refund;

import lombok.Getter;

@Getter
public enum RefundReason {
	CHANGE_OF_MIND("단순 변심"),
	DEFECT("파손"),
	DAMAGED("파본"),
	WRONG_DELIVERY("오배송"),
	OTHER("기타");

	private final String description;

	RefundReason(String description) {
		this.description = description;
	}
}
