package shop.bluebooktle.common.dto.coupon.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CouponRegisterRequest {
	@NotNull(message = "쿠폰 정책 선택은 필수입니다.")
	Long couponTypeId;
	@NotBlank(message = "쿠폰 이름은 필수입니다.")
	String name;

	Long bookId;
	Long categoryId;

}
