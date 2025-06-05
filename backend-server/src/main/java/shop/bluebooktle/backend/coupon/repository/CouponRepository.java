package shop.bluebooktle.backend.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponQueryRepository {
	Optional<Coupon> findByCouponName(String couponName);
}
