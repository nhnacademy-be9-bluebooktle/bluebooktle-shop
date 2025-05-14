package shop.bluebooktle.common.exception.order;

public class DeliveryRuleNotFoundException extends RuntimeException {
	public DeliveryRuleNotFoundException() {
		super("해당 정책이 존재하지 않습니다.");
	}
}
