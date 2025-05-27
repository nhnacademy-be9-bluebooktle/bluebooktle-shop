package shop.bluebooktle.common.domain.point;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public enum PointSourceTypeEnum {
	LOGIN_EARN(1L, ActionType.EARN, "로그인 적립"),
	SIGNUP_EARN(2L, ActionType.EARN, "회원 가입"),
	REVIEW_EARN(3L, ActionType.EARN, "리뷰 적립"),
	PAYMENT_EARN(4L, ActionType.EARN, "결제 적립"),
	PAYMENT_USE(5L, ActionType.USE, "결제 사용");

	private final Long id;
	private final ActionType actionType;
	private final String sourceType;

	PointSourceTypeEnum(Long id, ActionType actionType, String sourceType) {
		this.id = id;
		this.actionType = actionType;
		this.sourceType = sourceType;
	}

	private static final Map<Long, PointSourceTypeEnum> BY_ID =
		Arrays.stream(values()).collect(Collectors.toMap(PointSourceTypeEnum::getId, e -> e));

	public static PointSourceTypeEnum fromId(Long id) {
		PointSourceTypeEnum type = BY_ID.get(id);
		if (type == null)
			throw new IllegalArgumentException("Unknown id: " + id);
		return type;
	}
}
