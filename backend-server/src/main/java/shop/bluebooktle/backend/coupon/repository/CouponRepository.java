package shop.bluebooktle.backend.coupon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
	//이름 존재 여부 확인
	boolean existsByCouponName(String name);

	@Query("""
		    SELECT new shop.bluebooktle.common.dto.coupon.response.CouponResponse(
		        c.id,
		        c.couponName,
		        ct.name,
		        ct.target,
		        c.availableStartAt,
		        c.availableEndAt,
		        c.createdAt
		    )
		    FROM Coupon c
		    JOIN c.couponType ct
		""")
	Page<CouponResponse> findAllWithCouponType(Pageable pageable);

	/*
	TODO [쿠폰] queryDSL 적용 예정
	1. 전체 쿠폰
	2. target = BOOK 쿠폰
	3. target = ORDER 쿠폰
	4. 도서 관련 쿠폰
	5. 카테고리 관련 쿠폰
	 */

	// TODO [쿠폰] 쿠폰 상세 페이지 - 관리자
	@Query("select c from Coupon c join fetch c.couponType")
	Coupon findByCouponId(Long id);
}
