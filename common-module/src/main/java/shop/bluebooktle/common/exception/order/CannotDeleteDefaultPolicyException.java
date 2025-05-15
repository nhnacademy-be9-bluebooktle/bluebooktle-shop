package shop.bluebooktle.common.exception.order;

public class CannotDeleteDefaultPolicyException extends RuntimeException {
	public CannotDeleteDefaultPolicyException() {
		super("기본 배송 정책은 삭제할 수 없습니다.");
	}
}
