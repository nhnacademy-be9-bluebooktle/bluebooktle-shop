package shop.bluebooktle.backend.coupon.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponIssueMessage {
	private Long userId;
	private Long couponId;
	private LocalDateTime availableStartAt;
	private LocalDateTime availableEndAt;
}
