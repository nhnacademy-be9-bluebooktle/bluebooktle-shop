package shop.bluebooktle.backend.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.UserCoupon;

public interface UserCouponRepository
	extends JpaRepository<UserCoupon, Long>, CouponQueryRepository, UserCouponQueryRepository {
}
