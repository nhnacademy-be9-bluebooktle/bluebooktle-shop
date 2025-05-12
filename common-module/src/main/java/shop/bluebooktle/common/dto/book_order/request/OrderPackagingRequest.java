package shop.bluebooktle.common.dto.book_order.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class OrderPackagingRequest {

	@NotNull(message = "도서 주문 ID는 필수값입니다.")
	Long bookOrderId;

	@NotNull(message = "포장 옵션 ID는 필수값입니다.")
	Long packagingOptionId;

	@Min(value = 1, message = "포장 수량은 1개 이상이어야 합니다.")
	int quantity;
}
