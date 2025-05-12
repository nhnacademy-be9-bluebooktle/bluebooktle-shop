package shop.bluebooktle.common.dto.book_order.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class BookOrderRequest {

	@NotNull(message = "도서 주문 ID는 필수값입니다.")
	Long bookOrderId;

	@NotNull(message = "도서 ID는 필수값입니다.")
	Long bookId;

	@NotNull(message = "주문 ID는 필수값입니다.")
	Long orderId;

	@Min(value = 1, message = "도서 수량은 1개 이상이어야 합니다.")
	int quantity;

	@NotNull(message = "가격은 필수입니다.")
	BigDecimal price;
}
