package shop.bluebooktle.common.dto.coupon.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserCouponRegisterRequest {
	@NotNull(message = "쿠폰 ID는 필수입니다.")
	Long couponId;
	@NotNull(message = "사용 가능 시작일은 필수입니다.")
	LocalDateTime availableStartAt;
	@NotNull(message = "사용 가능 종료일은 필수입니다.")
	LocalDateTime availableEndAt;
}
