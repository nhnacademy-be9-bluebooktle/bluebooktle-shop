package shop.bluebooktle.common.dto.cart.request;

import java.util.List;

public record GuestCartRemoveSelectedRequest(
	String id,
	List<Long> bookIds
) {
}
