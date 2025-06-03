package shop.bluebooktle.backend.coupon.repository.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.QBook;
import shop.bluebooktle.backend.book.entity.QBookCategory;
import shop.bluebooktle.backend.book.entity.QCategory;
import shop.bluebooktle.backend.coupon.entity.QAbsoluteCoupon;
import shop.bluebooktle.backend.coupon.entity.QBookCoupon;
import shop.bluebooktle.backend.coupon.entity.QCategoryCoupon;
import shop.bluebooktle.backend.coupon.entity.QCoupon;
import shop.bluebooktle.backend.coupon.entity.QCouponType;
import shop.bluebooktle.backend.coupon.entity.QRelativeCoupon;
import shop.bluebooktle.backend.coupon.entity.QUserCoupon;
import shop.bluebooktle.backend.coupon.repository.UserCouponQueryRepository;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.response.QUsableUserCouponResponse;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponResponse;

@Repository
@RequiredArgsConstructor
public class UserCouponQueryRepositoryImpl implements UserCouponQueryRepository {

	private final JPAQueryFactory queryFactory;
	private static final Long ORDER_COUPON_KEY = -1L; // 주문 관련 쿠폰일 경우 key = -1

	@Override
	public UsableUserCouponMapResponse findAllByUsableUserCouponForOrder(Long userId, List<Long> bookIds) {
		QUserCoupon userCoupon = QUserCoupon.userCoupon;
		QCoupon coupon = QCoupon.coupon;
		QCouponType couponType = QCouponType.couponType;
		QBookCoupon bookCoupon = QBookCoupon.bookCoupon;
		QCategoryCoupon categoryCoupon = QCategoryCoupon.categoryCoupon;
		QBook book = QBook.book;
		QCategory category = QCategory.category;
		QAbsoluteCoupon absoluteCoupon = QAbsoluteCoupon.absoluteCoupon;
		QRelativeCoupon relativeCoupon = QRelativeCoupon.relativeCoupon;

		Map<Long, List<UsableUserCouponResponse>> resultMap = new HashMap<>();

		BooleanBuilder builder = new BooleanBuilder()
			.and(userCoupon.user.id.eq(userId))
			.and(userCoupon.usedAt.isNull())
			.and(userCoupon.availableStartAt.loe(LocalDateTime.now()))
			.and(userCoupon.availableEndAt.goe(LocalDateTime.now()));

		//주문 쿠폰 조회
		List<UsableUserCouponResponse> orderCoupons = queryFactory
			.select(new QUsableUserCouponResponse(
				userCoupon.id,
				coupon.id,
				coupon.couponName,
				couponType.name,
				couponType.minimumPayment,
				absoluteCoupon.discountPrice,
				relativeCoupon.maximumDiscountPrice,
				relativeCoupon.discountPercent,
				book.title.coalesce((String)null),
				category.name.coalesce((String)null)
			))
			.from(userCoupon)
			.join(userCoupon.coupon, coupon)
			.join(coupon.couponType, couponType)
			.leftJoin(absoluteCoupon)
			.on(absoluteCoupon.id.eq(couponType.id))
			.leftJoin(relativeCoupon)
			.on(relativeCoupon.id.eq(couponType.id))
			.leftJoin(bookCoupon).on(bookCoupon.coupon.eq(coupon))
			.leftJoin(bookCoupon.book, book)
			.leftJoin(categoryCoupon).on(categoryCoupon.coupon.eq(coupon))
			.leftJoin(categoryCoupon.category, category)
			.where(
				builder.and(couponType.target.eq(CouponTypeTarget.ORDER))
			)
			.fetch();

		resultMap.put(ORDER_COUPON_KEY, orderCoupons);

		// 도서의 카테고리 조회
		Map<Long, List<String>> bookCategoryPaths = getBookCategoryPathMap(bookIds);

		for (Long bookId : bookIds) {
			List<String> categoryPaths = bookCategoryPaths.get(bookId);
			if (categoryPaths == null || categoryPaths.isEmpty()) {
				resultMap.put(bookId, Collections.emptyList());
				continue;
			}

			BooleanBuilder likeBuilder = new BooleanBuilder();
			for (String path : categoryPaths) {
				likeBuilder.or(category.categoryPath.like(path + "%"));
			}

			List<UsableUserCouponResponse> bookCoupons = queryFactory
				.select(new QUsableUserCouponResponse(
					userCoupon.id,
					coupon.id,
					coupon.couponName,
					couponType.name,
					couponType.minimumPayment,
					absoluteCoupon.discountPrice,
					relativeCoupon.maximumDiscountPrice,
					relativeCoupon.discountPercent,
					bookCoupon.book.title,
					categoryCoupon.category.name
				))
				.from(userCoupon)
				.join(userCoupon.coupon, coupon)
				.join(coupon.couponType, couponType)
				.join(categoryCoupon)
				.on(categoryCoupon.coupon.id.eq(coupon.id))
				.join(category)
				.on(categoryCoupon.category.id.eq(category.id))
				.leftJoin(absoluteCoupon)
				.on(absoluteCoupon.id.eq(couponType.id))
				.leftJoin(relativeCoupon)
				.on(relativeCoupon.id.eq(couponType.id))
				.where(builder.and(couponType.target.eq(CouponTypeTarget.BOOK))
					.and(likeBuilder))
				.fetch();

			resultMap.put(bookId, bookCoupons);
		}

		UsableUserCouponMapResponse response = new UsableUserCouponMapResponse();
		response.setUsableUserCouponMap(resultMap);
		return response;
	}

	private Map<Long, List<String>> getBookCategoryPathMap(List<Long> bookIds) {
		QBook book = QBook.book;
		QBookCategory bookCategory = QBookCategory.bookCategory;
		QCategory category = QCategory.category;

		return queryFactory
			.select(book.id, category.categoryPath)
			.from(book)
			.join(bookCategory).on(bookCategory.book.id.eq(book.id))
			.join(category).on(bookCategory.category.id.eq(category.id))
			.where(book.id.in(bookIds))
			.fetch()
			.stream()
			.collect(Collectors.groupingBy(
				t -> t.get(book.id),
				Collectors.mapping(t -> t.get(category.categoryPath), Collectors.toList())
			));
	}
}

