package shop.bluebooktle.common.dto.coupon.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CouponUpdateRequest {
	@NotBlank(message = "쿠폰 이름은 필수입니다.")
	@Size(max = 100, message = "쿠폰명은 100자 이하로 입력해주세요.")
	String name;

	Long bookId;
	Long categoryId;
}
