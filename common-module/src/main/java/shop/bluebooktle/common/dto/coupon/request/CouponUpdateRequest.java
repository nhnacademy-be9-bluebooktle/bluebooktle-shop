package shop.bluebooktle.common.dto.coupon.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CouponUpdateRequest {
	@NotBlank
	String name;
	@NotNull
	LocalDateTime availableStartAt;
	@NotNull
	LocalDateTime availableEndAt;

	Long bookId;
	Long categoryId;
}
