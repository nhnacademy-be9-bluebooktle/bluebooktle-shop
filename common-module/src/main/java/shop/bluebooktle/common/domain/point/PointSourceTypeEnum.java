package shop.bluebooktle.common.domain.point;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import shop.bluebooktle.common.exception.point.PointSourceNotFountException;

@Getter
public enum PointSourceTypeEnum {
	LOGIN_EARN(1L, ActionType.EARN, "로그인 적립"),
	SIGNUP_EARN(2L, ActionType.EARN, "회원 가입"),
	REVIEW_EARN(3L, ActionType.EARN, "리뷰 적립"),
	PAYMENT_EARN(4L, ActionType.EARN, "결제 적립"),
	PAYMENT_USE(5L, ActionType.USE, "결제 사용"),
	ORDER_CANCEL(6L, ActionType.EARN, "주문 취소 환불"),
	PAYMENT_CANCEL(7L, ActionType.USE, "결제 취소 회수");

	private static final Map<Long, PointSourceTypeEnum> BY_ID =
		Arrays.stream(values()).collect(Collectors.toMap(PointSourceTypeEnum::getId, e -> e));
	private final Long id;
	private final ActionType actionType;
	private final String sourceType;

	PointSourceTypeEnum(Long id, ActionType actionType, String sourceType) {
		this.id = id;
		this.actionType = actionType;
		this.sourceType = sourceType;
	}

	public static PointSourceTypeEnum fromId(Long id) {
		PointSourceTypeEnum type = BY_ID.get(id);
		if (type == null)
			throw new PointSourceNotFountException();
		return type;
	}
}
