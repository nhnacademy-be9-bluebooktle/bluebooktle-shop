package shop.bluebooktle.common.domain.point;

import lombok.Getter;

@Getter
public enum PolicyType {
	AMOUNT("금액"),
	PERCENTAGE("비율");

	public final String displayName;

	PolicyType(String displayName) {
		this.displayName = displayName;
	}
}
