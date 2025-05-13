package shop.bluebooktle.common.dto.book_order.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class BookOrderUpdateRequest {
	@NotNull(message = "도서 ID는 필수값입니다.")
	@Positive(message = "도서 ID는 양수여야 합니다.")
	Long bookOrderId;

	@NotNull(message = "도서 ID는 필수값입니다.")
	@Positive(message = "도서 ID는 양수여야 합니다.")
	Long bookId;

	@NotNull(message = "도서 ID는 필수값입니다.")
	@Positive(message = "도서 ID는 양수여야 합니다.")
	Long orderId;

	@Min(value = 1, message = "도서 수량은 1개 이상이어야 합니다.")
	int quantity;

	@DecimalMin(value = "0.0", inclusive = false, message = "가격은 0원보다 커야 합니다.")
	BigDecimal price;
}
