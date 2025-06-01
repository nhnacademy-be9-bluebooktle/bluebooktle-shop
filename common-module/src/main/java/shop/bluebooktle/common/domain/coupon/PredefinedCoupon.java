package shop.bluebooktle.common.domain.coupon;

public enum PredefinedCoupon {
	BIRTHDAY(1L),
	WELCOME(2L);

	private final Long id;

	PredefinedCoupon(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
