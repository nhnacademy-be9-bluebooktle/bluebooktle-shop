package shop.bluebooktle.common.dto.book_order.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class PackagingOptionRequest {
	@NotNull(message = "포장 옵션 ID는 필수입니다.")
	Long packagingOptionId;

	@NotBlank(message = "포장 옵션 이름은 필수입니다.")
	String name;

	@DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다.")
	BigDecimal price;
}
