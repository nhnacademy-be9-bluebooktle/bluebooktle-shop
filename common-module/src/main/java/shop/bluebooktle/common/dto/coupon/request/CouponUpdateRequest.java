package shop.bluebooktle.common.dto.coupon.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CouponUpdateRequest {
	@NotBlank(message = "쿠폰 이름은 필수입니다.")
	String name;

	Long bookId;
	Long categoryId;
}
