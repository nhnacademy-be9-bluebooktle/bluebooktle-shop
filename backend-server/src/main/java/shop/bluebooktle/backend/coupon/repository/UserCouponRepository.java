package shop.bluebooktle.backend.coupon.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.common.entity.auth.User;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, CouponQueryRepository {
	boolean existsByUserAndCouponAndAvailableStartAtBetween(User user, Coupon coupon, LocalDateTime start,
		LocalDateTime end);
}
