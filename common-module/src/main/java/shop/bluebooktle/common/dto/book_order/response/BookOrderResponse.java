package shop.bluebooktle.common.dto.book_order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class BookOrderResponse {
	Long bookOrderId;
	Long orderId;
	Long bookId;
	int quantity;
	BigDecimal price;
	LocalDateTime createdAt;
}
