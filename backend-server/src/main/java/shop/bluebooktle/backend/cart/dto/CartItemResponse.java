package shop.bluebooktle.backend.cart.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import shop.bluebooktle.backend.cart.entity.CartBook;

@Value
@Builder
@AllArgsConstructor
public class CartItemResponse implements Serializable {
	Long bookId;
	int quantity;

	public static CartItemResponse from(CartBook cartBook) {
		return new CartItemResponse(
			cartBook.getBook().getId(),
			cartBook.getQuantity()
		);
	}
}