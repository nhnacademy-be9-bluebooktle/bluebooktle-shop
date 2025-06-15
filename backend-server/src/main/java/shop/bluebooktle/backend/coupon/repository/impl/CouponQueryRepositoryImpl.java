package shop.bluebooktle.backend.coupon.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.QBook;
import shop.bluebooktle.backend.book.entity.QCategory;
import shop.bluebooktle.backend.coupon.entity.QAbsoluteCoupon;
import shop.bluebooktle.backend.coupon.entity.QBookCoupon;
import shop.bluebooktle.backend.coupon.entity.QCategoryCoupon;
import shop.bluebooktle.backend.coupon.entity.QCoupon;
import shop.bluebooktle.backend.coupon.entity.QCouponType;
import shop.bluebooktle.backend.coupon.entity.QRelativeCoupon;
import shop.bluebooktle.backend.coupon.entity.QUserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponQueryRepository;
import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.QCouponResponse;
import shop.bluebooktle.common.dto.coupon.response.QCouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.QUserCouponResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;

@Slf4j
@RequiredArgsConstructor
public class CouponQueryRepositoryImpl implements CouponQueryRepository {

	private final JPAQueryFactory queryFactory;

	//관리자 쿠폰 전체 조회
	@Override
	public Page<CouponResponse> findAllByCoupon(String searchCouponName, Pageable pageable) {
		QCoupon coupon = QCoupon.coupon;
		QCouponType couponType = QCouponType.couponType;
		QBookCoupon bookCoupon = QBookCoupon.bookCoupon;
		QCategoryCoupon categoryCoupon = QCategoryCoupon.categoryCoupon;

		QBook book = QBook.book;
		QCategory category = QCategory.category;

		BooleanBuilder builder = new BooleanBuilder();

		if (StringUtils.hasText(searchCouponName)) {
			builder.and(coupon.couponName.containsIgnoreCase(searchCouponName));
		}

		List<CouponResponse> content = queryFactory
			.select(new QCouponResponse(
				coupon.id,
				coupon.couponName,
				couponType.target,
				couponType.name,
				couponType.minimumPayment,
				coupon.createdAt,
				category.name,
				book.title
			))
			.from(coupon)
			.join(coupon.couponType, couponType)
			.leftJoin(bookCoupon).on(bookCoupon.coupon.eq(coupon))
			.leftJoin(bookCoupon.book, book)
			.leftJoin(categoryCoupon).on(categoryCoupon.coupon.eq(coupon))
			.leftJoin(categoryCoupon.category, category)
			.where(builder)
			.orderBy(coupon.id.asc())
			.offset(pageable.getOffset())
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

	// 관리자 쿠폰 정책 전체 조회
	@Override
	public Page<CouponTypeResponse> findAllByCouponType(Pageable pageable) {
		QCouponType couponType = QCouponType.couponType;
		QAbsoluteCoupon absoluteCoupon = QAbsoluteCoupon.absoluteCoupon;
		QRelativeCoupon relativeCoupon = QRelativeCoupon.relativeCoupon;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(couponType.deletedAt.isNull());

		List<CouponTypeResponse> content = queryFactory.select(new QCouponTypeResponse(
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

	//유저 쿠폰 조회
	@Override
	public Page<UserCouponResponse> findAllByUserCoupon(Long userId, UserCouponFilterType filterType,
		Pageable pageable) {
		QUserCoupon userCoupon = QUserCoupon.userCoupon;
		QCoupon coupon = QCoupon.coupon;
		QCouponType couponType = QCouponType.couponType;
		QBookCoupon bookCoupon = QBookCoupon.bookCoupon;
		QCategoryCoupon categoryCoupon = QCategoryCoupon.categoryCoupon;
		QBook book = QBook.book;
		QCategory category = QCategory.category;

		LocalDateTime now = LocalDateTime.now();

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(userCoupon.user.id.eq(userId));

		// 조회 필터
		switch (filterType) {
			case USABLE -> builder
				.and(userCoupon.availableStartAt.loe(now))
				.and(userCoupon.availableEndAt.goe(now))
				.and(userCoupon.usedAt.isNull());
			case USED -> builder.and(userCoupon.usedAt.isNotNull());
			case EXPIRED -> builder
				.and(userCoupon.usedAt.isNull())
				.and(userCoupon.availableEndAt.lt(now));
			case ALL -> {
			}
		}
		//정렬
		NumberExpression<Integer> priority = new CaseBuilder()
			.when(userCoupon.usedAt.isNull().and(userCoupon.availableEndAt.goe(now))).then(0) // 사용 가능
			.when(userCoupon.usedAt.isNotNull()).then(1) // 사용 완료
			.otherwise(2); // 기간 만료

		List<UserCouponResponse> content = queryFactory
			.select(new QUserCouponResponse(
				userCoupon.id,
				userCoupon.createdAt,
				coupon.couponName,
				couponType.name,
				couponType.target,
				userCoupon.availableStartAt,
				userCoupon.availableEndAt,
				userCoupon.usedAt,

				book.title,
				category.name
			))
			.from(userCoupon)
			.join(userCoupon.coupon, coupon)
			.join(coupon.couponType, couponType)
			.leftJoin(bookCoupon).on(bookCoupon.coupon.eq(coupon))
			.leftJoin(book).on(bookCoupon.book.eq(book))
			.leftJoin(categoryCoupon).on(categoryCoupon.coupon.eq(coupon))
			.leftJoin(category).on(categoryCoupon.category.eq(category))
			.where(builder)
			.orderBy(
				priority.asc(),
				userCoupon.availableEndAt.asc().nullsLast(),
				userCoupon.usedAt.desc().nullsLast()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(userCoupon.count())
			.from(userCoupon)
			.join(userCoupon.coupon, coupon)
			.join(coupon.couponType, couponType)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0);
	}

}
