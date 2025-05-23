package shop.bluebooktle.backend.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.RelativeCoupon;

public interface RelativeCouponRepository extends JpaRepository<RelativeCoupon, Long> {
}
