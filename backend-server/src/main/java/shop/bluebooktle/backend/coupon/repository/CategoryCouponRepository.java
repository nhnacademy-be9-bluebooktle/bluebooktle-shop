package shop.bluebooktle.backend.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.CategoryCoupon;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {
	Optional<CategoryCoupon> findByCouponId(Long couponId);
}
