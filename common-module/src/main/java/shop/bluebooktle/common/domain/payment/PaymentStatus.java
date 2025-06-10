package shop.bluebooktle.common.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
	READY("준비"),
	IN_PROGRESS("진행중"),
	WAITING_FOR_DEPOSIT("입금대기"),
	DONE("결제완료"),
	CANCELED("결제취소"),
	PARTIAL_CANCELED("부분취소"),
	ABORTED("결제승인실패"),
	EXPIRED("결제유효시간만료");

	private final String description;
}
