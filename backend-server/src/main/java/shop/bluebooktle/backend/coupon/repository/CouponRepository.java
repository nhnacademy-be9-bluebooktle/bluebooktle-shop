package shop.bluebooktle.backend.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shop.bluebooktle.backend.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponQueryRepository {
	//이름 존재 여부 확인
	boolean existsByCouponName(String name);

	// TODO [쿠폰] 쿠폰 상세 페이지 - 관리자
	@Query("select c from Coupon c join fetch c.couponType")
	Coupon findByCouponId(Long id);
}
