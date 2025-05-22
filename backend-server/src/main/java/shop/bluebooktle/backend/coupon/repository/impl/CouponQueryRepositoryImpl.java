package shop.bluebooktle.backend.coupon.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.QBook;
import shop.bluebooktle.backend.book.entity.QCategory;
import shop.bluebooktle.backend.coupon.dto.CouponSearchRequest;
import shop.bluebooktle.backend.coupon.entity.QAbsoluteCoupon;
import shop.bluebooktle.backend.coupon.entity.QBookCoupon;
import shop.bluebooktle.backend.coupon.entity.QCategoryCoupon;
import shop.bluebooktle.backend.coupon.entity.QCoupon;
import shop.bluebooktle.backend.coupon.entity.QCouponType;
import shop.bluebooktle.backend.coupon.entity.QRelativeCoupon;
import shop.bluebooktle.backend.coupon.entity.QUserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponQueryRepository;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.QCouponResponse;
import shop.bluebooktle.common.dto.coupon.response.QCouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.QUserCouponResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.entity.auth.User;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CouponQueryRepositoryImpl implements CouponQueryRepository {

	private final JPAQueryFactory queryFactory;

	//관리자 쿠폰 전체 조회
	@Override
	public Page<CouponResponse> findAllByCoupon(CouponSearchRequest request, Pageable pageable) {
		QCoupon coupon = QCoupon.coupon;
		QCouponType couponType = QCouponType.couponType;
		QBookCoupon bookCoupon = QBookCoupon.bookCoupon;
		QCategoryCoupon categoryCoupon = QCategoryCoupon.categoryCoupon;

		QBook book = QBook.book;
		QCategory category = QCategory.category;

		BooleanBuilder builder = new BooleanBuilder();

		if (request.getTarget() != null) {
			builder.and(couponType.target.eq(request.getTarget()));
		}

		if (request.getBookId() != null) {
			builder.and(bookCoupon.book.id.eq(request.getBookId()));
		}
		if (request.getCategoryId() != null) {
			builder.and(categoryCoupon.category.id.eq(request.getCategoryId()));
		}

		List<CouponResponse> content = queryFactory.select(new QCouponResponse(
				coupon.id,
				coupon.couponName,
				couponType.target,
				couponType.name,
				couponType.minimumPayment,
				coupon.createdAt,
				categoryCoupon.category.name,
				bookCoupon.book.title
			))
			.from(coupon)
			.join(coupon.couponType, couponType)
			.leftJoin(bookCoupon).on(bookCoupon.coupon.eq(coupon))
			.leftJoin(bookCoupon.book, book)
			.leftJoin(categoryCoupon).on(categoryCoupon.coupon.eq(coupon))
			.leftJoin(categoryCoupon.category, category)
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

	//유저 별 쿠폰 전체 조회
	@Override
	public Page<UserCouponResponse> findAllByUserCoupon(User user, Pageable pageable) {
		QUserCoupon userCoupon = QUserCoupon.userCoupon;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(userCoupon.user.eq(user));
		return findUserCoupon(builder, pageable);
	}

	//유저 별 사용 가능 쿠폰 전체 조회
	@Override
	public Page<UserCouponResponse> findAllByUsableUserCoupon(User user, Pageable pageable) {
		QUserCoupon userCoupon = QUserCoupon.userCoupon;

		LocalDateTime now = LocalDateTime.now();
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(userCoupon.user.eq(user))
			.and(userCoupon.availableStartAt.loe(now))
			.and(userCoupon.availableEndAt.goe(now));
		return findUserCoupon(builder, pageable);
	}

	// 유저 별 사용 완료 쿠폰 전체 조회
	@Override
	public Page<UserCouponResponse> findAllByUsedUserCoupon(User user, Pageable pageable) {
		QUserCoupon userCoupon = QUserCoupon.userCoupon;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(userCoupon.user.eq(user)).and(userCoupon.usedAt.isNotNull());
		return findUserCoupon(builder, pageable);
	}

	// 유저 별 (사용 못함 && 기간 만료) 쿠폰 전체 조회
	@Override
	public Page<UserCouponResponse> findAllByExpiredUserCoupon(User user, Pageable pageable) {
		QUserCoupon userCoupon = QUserCoupon.userCoupon;

		LocalDateTime now = LocalDateTime.now();
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(userCoupon.user.eq(user)).and(userCoupon.usedAt.isNull()).and(userCoupon.availableEndAt.lt(now));
		return findUserCoupon(builder, pageable);
	}

	//유저 별 쿠폰 조회 공통
	private Page<UserCouponResponse> findUserCoupon(BooleanBuilder builder, Pageable pageable) {
		QCoupon coupon = QCoupon.coupon;
		QUserCoupon userCoupon = QUserCoupon.userCoupon;
		QCouponType couponType = QCouponType.couponType;

		List<UserCouponResponse> content = queryFactory.select(new QUserCouponResponse(
				userCoupon.id,
				userCoupon.createdAt,
				coupon.couponName,
				couponType.name,
				couponType.target,
				userCoupon.availableStartAt,
				userCoupon.availableEndAt,
				userCoupon.usedAt
			))
			.from(userCoupon)
			.join(userCoupon.coupon, coupon)
			.join(coupon.couponType, couponType)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		Long total = queryFactory
			.select(userCoupon.count())
			.from(userCoupon)
			.where(builder)
			.fetchOne();
		return new PageImpl<>(content, pageable, total != null ? total : 0);
	}

}
