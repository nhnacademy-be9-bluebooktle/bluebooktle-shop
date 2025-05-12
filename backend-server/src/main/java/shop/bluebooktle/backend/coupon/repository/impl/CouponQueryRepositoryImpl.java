package shop.bluebooktle.backend.coupon.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.dto.CouponSearchRequest;
import shop.bluebooktle.backend.coupon.entity.QAbsoluteCoupon;
import shop.bluebooktle.backend.coupon.entity.QCoupon;
import shop.bluebooktle.backend.coupon.entity.QCouponType;
import shop.bluebooktle.backend.coupon.entity.QRelativeCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponQueryRepository;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;

@Repository
@RequiredArgsConstructor
public class CouponQueryRepositoryImpl implements CouponQueryRepository {

	private final JPAQueryFactory queryFactory;

	//쿠폰 전체 조회
	@Override
	public Page<CouponResponse> findAllByCoupon(CouponSearchRequest request, Pageable pageable) {
		QCoupon coupon = QCoupon.coupon;
		QCouponType couponType = QCouponType.couponType;

		BooleanBuilder builder = new BooleanBuilder();

		if (request.getTarget() != null) {
			builder.and(couponType.target.eq(request.getTarget())); // (ORDER, BOOK)
		}
		if (Boolean.TRUE.equals(request.getActive())) { // 유효 기간
			LocalDateTime now = LocalDateTime.now();
			builder.and(coupon.availableStartAt.loe(now)); // start <= now
			builder.and(coupon.availableEndAt.goe(now)); // end >= now
		}

		List<CouponResponse> content = queryFactory
			.select(Projections.constructor(CouponResponse.class,
				coupon.id,
				coupon.couponName,
				couponType.name,
				couponType.target,
				coupon.availableStartAt,
				coupon.availableEndAt,
				coupon.createdAt
			))
			.from(coupon)
			.join(coupon.couponType, couponType)
			.where(builder)
			.offset(pageable.getOffset()) // offset/limit 페이징 처리
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(coupon.id.count())
			.from(coupon)
			.join(coupon.couponType, couponType)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0);
	}

	//쿠폰 정책 전체 조회
	@Override
	public Page<CouponTypeResponse> findAllByCouponType(Pageable pageable) {
		QCouponType couponType = QCouponType.couponType;
		QAbsoluteCoupon absoluteCoupon = QAbsoluteCoupon.absoluteCoupon;
		QRelativeCoupon relativeCoupon = QRelativeCoupon.relativeCoupon;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(couponType.deletedAt.isNull());

		List<CouponTypeResponse> content = queryFactory.select(Projections.constructor(CouponTypeResponse.class,
				couponType.id,
				couponType.name,
				couponType.target,
				couponType.minimumPayment,
				absoluteCoupon.discountPrice,
				relativeCoupon.maximumDiscountPrice,
				relativeCoupon.discountPercent
			))
			.from(couponType)
			.leftJoin(absoluteCoupon).on(absoluteCoupon.id.eq(couponType.id))
			.leftJoin(relativeCoupon).on(relativeCoupon.id.eq(couponType.id))
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		Long total = queryFactory
			.select(couponType.id.count())
			.from(couponType)
			.where(couponType.deletedAt.isNull())
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0);
	}
}
