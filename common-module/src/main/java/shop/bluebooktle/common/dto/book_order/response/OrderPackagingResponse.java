package shop.bluebooktle.common.dto.book_order.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class OrderPackagingResponse {
	Long orderPackagingId;
	Long bookOrderId;
	Long packagingOptionId;
	int quantity;
	LocalDateTime createdAt;
}
