package shop.bluebooktle.common.dto.book_order.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UserCouponBookOrderRequest {

	@NotNull(message = "orderId는 필수입니다.")
	private Long orderId;

	@NotNull(message = "userId는 필수입니다.")
	private Long userId;

	@NotNull(message = "쿠폰 사용 목록은 필수입니다.")
	private List<CouponUsageDto> usages;

	@Getter
	@Builder
	public static class CouponUsageDto {
		@NotNull(message = "userCouponId는 필수입니다.")
		private Long userCouponId;

		private Long bookOrderId;
	}
}