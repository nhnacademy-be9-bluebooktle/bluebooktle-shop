package shop.bluebooktle.backend.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.BookCoupon;
import shop.bluebooktle.backend.coupon.entity.Coupon;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
	Optional<BookCoupon> findByCoupon(Coupon coupon);

	void deleteByCoupon(Coupon coupon);
}
