package shop.bluebooktle.backend.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.AbsoluteCoupon;

public interface AbsoluteCouponRepository extends JpaRepository<AbsoluteCoupon, Long> {
	Optional<AbsoluteCoupon> findByCouponTypeId(Long couponTypeId);
}
