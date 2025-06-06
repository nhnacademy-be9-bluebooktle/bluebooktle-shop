package shop.bluebooktle.common.dto.book_order.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class PackagingOptionRequest {
	@NotNull(message = "포장 옵션 이름은 필수입니다.")
	@Size(max = 20, message = "이름은 20자를 넘어갈 수 없습니다.")
	String name;

	@DecimalMin(value = "0.0", inclusive = false, message = "가격은 0원보다 커야 합니다.")
	BigDecimal price;
}
