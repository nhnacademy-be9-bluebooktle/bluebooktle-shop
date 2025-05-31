package shop.bluebooktle.backend.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.RelativeCoupon;

public interface RelativeCouponRepository extends JpaRepository<RelativeCoupon, Long> {
	Optional<RelativeCoupon> findByCouponTypeId(Long couponTypeId);
}
