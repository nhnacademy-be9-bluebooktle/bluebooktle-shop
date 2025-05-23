package shop.bluebooktle.backend.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.AbsoluteCoupon;

public interface AbsoluteCouponRepository extends JpaRepository<AbsoluteCoupon, Long> {
}
