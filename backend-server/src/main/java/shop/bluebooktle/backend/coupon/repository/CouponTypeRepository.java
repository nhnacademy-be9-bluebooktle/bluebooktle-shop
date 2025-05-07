package shop.bluebooktle.backend.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.CouponType;

public interface CouponTypeRepository extends JpaRepository<CouponType, Long> {
}
