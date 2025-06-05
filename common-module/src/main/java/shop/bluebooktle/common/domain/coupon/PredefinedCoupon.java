package shop.bluebooktle.common.domain.coupon;

import lombok.Getter;

@Getter
public enum PredefinedCoupon {
	BIRTHDAY(1L),
	WELCOME(2L);

	private final Long id;

	PredefinedCoupon(Long id) {
		this.id = id;
	}

}
