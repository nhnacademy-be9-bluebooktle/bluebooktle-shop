package shop.bluebooktle.common.dto.order.response;

import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;

public record OrderCreateResponse(
	BookCartOrderResponse bookCartOrderResponse
) {
}
