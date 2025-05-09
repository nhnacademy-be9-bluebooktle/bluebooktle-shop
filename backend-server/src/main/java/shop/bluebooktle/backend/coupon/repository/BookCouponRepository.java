package shop.bluebooktle.backend.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.BookCoupon;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
	Optional<BookCoupon> findByCouponId(Long couponId);
}
