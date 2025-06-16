package shop.bluebooktle.backend.coupon.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.UserCoupon;

public interface UserCouponRepository
	extends JpaRepository<UserCoupon, Long>, CouponQueryRepository, UserCouponQueryRepository {
	boolean existsByUserIdAndCouponIdAndAvailableStartAt(
		Long userId,
		Long couponId,
		LocalDateTime availableStartAt
	);
}
