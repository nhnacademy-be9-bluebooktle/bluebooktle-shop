package shop.bluebooktle.common.dto.coupon.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WelcomeCouponIssueMessage {
	private Long userId;
	private Long couponId;
	private LocalDateTime availableStartAt;
	private LocalDateTime availableEndAt;
}
