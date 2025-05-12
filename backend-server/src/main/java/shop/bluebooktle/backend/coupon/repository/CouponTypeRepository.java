package shop.bluebooktle.backend.coupon.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;

public interface CouponTypeRepository extends JpaRepository<CouponType, Long> {
	Optional<CouponType> findByName(String couponName);

	@Query("""
		    SELECT new shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse(
		        ct.id,
		        ct.name,
		        ct.target,
		        ct.minimumPayment,
		        ac.discountPrice,
		        rc.maximumDiscountPrice,
		        rc.discountPercent
		    )
		    FROM CouponType ct
		    LEFT JOIN AbsoluteCoupon ac ON ac.couponType.id = ct.id
		    LEFT JOIN RelativeCoupon rc ON rc.couponType.id = ct.id
		    WHERE ct.deletedAt IS NULL
		""")
	Page<CouponTypeResponse> findAllBy(Pageable pageable);
}
