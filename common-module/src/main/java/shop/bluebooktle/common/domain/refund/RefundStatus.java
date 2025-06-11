package shop.bluebooktle.common.domain.refund;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefundStatus {
	PENDING("처리 대기"),
	COMPLETE("환불 완료"),
	REJECTED("환불 거절");

	private final String description;
}