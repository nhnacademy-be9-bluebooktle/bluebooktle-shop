package shop.bluebooktle.backend.cart.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartOrderRequest {

	private List<OrderItem> items;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderItem {
		private Long bookId;
		private int quantity;
	}
}
