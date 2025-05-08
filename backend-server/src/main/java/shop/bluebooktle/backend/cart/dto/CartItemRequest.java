package shop.bluebooktle.backend.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest implements Serializable {
	private Long bookId;
	private int quantity;
}