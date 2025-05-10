package shop.bluebooktle.backend.coupon.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.coupon.entity.CouponType;

public interface CouponTypeRepository extends JpaRepository<CouponType, Long> {
	Optional<CouponType> findByName(String couponName);

	Page<CouponType> findAllCouponTypeList(Pageable pageable);
}
