package shop.bluebooktle.common.exception.order;

public class OrderStateNotFoundException extends RuntimeException {
	public OrderStateNotFoundException(String name) {
		super(name + "는 존재하지 않는 상태입니다");
	}
}
