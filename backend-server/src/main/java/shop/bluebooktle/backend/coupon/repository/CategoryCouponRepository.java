package shop.bluebooktle.backend.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.CategoryCoupon;
import shop.bluebooktle.backend.coupon.entity.Coupon;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {
	Optional<CategoryCoupon> findByCoupon(Coupon coupon);

	void deleteByCoupon(Coupon coupon);
}
