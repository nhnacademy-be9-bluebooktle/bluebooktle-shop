package shop.bluebooktle.common.dto.book_order.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class PackagingOptionRequest {
	@NotNull(message = "도서 포장 ID는 필수값입니다.")
	@Positive(message = "도서 포장 ID는 양수여야 합니다.")
	Long packagingOptionId;

	@NotNull(message = "포장 옵션 이름은 필수입니다.")
	@Max(value = 20, message = "이름은 20자를 넘어갈 수 없습니다.")
	String name;

	@DecimalMin(value = "0.0", inclusive = false, message = "가격은 0원보다 커야 합니다.")
	BigDecimal price;
}
