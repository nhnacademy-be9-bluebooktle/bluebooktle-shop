package shop.bluebooktle.backend.user.repository.impl;

import static shop.bluebooktle.common.entity.auth.QUser.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book_order.entity.QBookOrder;
import shop.bluebooktle.backend.book_order.entity.QOrderPackaging;
import shop.bluebooktle.backend.book_order.entity.QPackagingOption;
import shop.bluebooktle.backend.order.entity.QOrder;
import shop.bluebooktle.backend.payment.entity.QPayment;
import shop.bluebooktle.backend.user.dto.UserNetSpentAmountDto;
import shop.bluebooktle.backend.user.repository.UserQueryRepository;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.dto.user.response.UserMembershipLevelResponse;
import shop.bluebooktle.common.entity.auth.QMembershipLevel;
import shop.bluebooktle.common.entity.auth.QUser;
import shop.bluebooktle.common.entity.auth.User;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserQueryRepositoryImpl implements UserQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<User> findByBirthdayMonth(String month) {
		QUser user = QUser.user;

		BooleanBuilder builder = new BooleanBuilder();

		builder.and(Expressions.stringTemplate("substring({0}, 5, 2)", user.birth).eq(month))
			.and(user.status.eq(UserStatus.ACTIVE));

		List<User> users = queryFactory
			.selectFrom(user)
			.where(builder)
			.fetch();

		for (User u : users) {
			log.info("{}의 생일 월: {}", u.getLoginId(), u.getBirth());
		}
		return users;
	}

	@Override
	public Page<User> findUsersBySearchRequest(UserSearchRequest request, Pageable pageable) {

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(searchCondition(request.getSearchField(), request.getSearchKeyword()));
		builder.and(userTypeFilter(request.getUserTypeFilter()));
		builder.and(userStatusFilter(request.getUserStatusFilter()));

		List<User> content = queryFactory
			.selectFrom(user)
			.leftJoin(user.membershipLevel).fetchJoin()
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(getOrderSpecifier(pageable.getSort()))
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(user.count())
			.from(user)
			.where(builder);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	@Override
	public List<UserNetSpentAmountDto> findUserNetSpentAmountForLastThreeMonths() {
		QUser user = QUser.user;
		QOrder order = QOrder.order;
		QPayment payment = QPayment.payment;

		NumberExpression<BigDecimal> netAmountExpression = getNetAmountExpression(order, payment);

		return queryFactory
			.select(Projections.constructor(UserNetSpentAmountDto.class,
				user.id,
				netAmountExpression.sum().coalesce(BigDecimal.ZERO)
			))
			.from(user)
			.leftJoin(order).on(
				order.user.eq(user)
					.and(order.createdAt.after(LocalDateTime.now().minusMonths(3)))
					.and(order.deletedAt.isNull())
			)
			.leftJoin(payment).on(payment.order.id.eq(order.id))
			.groupBy(user.id)
			.fetch();
	}

	@Override
	public UserMembershipLevelResponse findUserNetSpentAmountForLastThreeMonthsByUserId(Long userId) {
		QUser user = QUser.user;
		QOrder order = QOrder.order;
		QPayment payment = QPayment.payment;
		QMembershipLevel membershipLevel = QMembershipLevel.membershipLevel;

		LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
		NumberExpression<BigDecimal> netAmountExpression = getNetAmountExpression(order, payment);

		BigDecimal netAmount = queryFactory
			.select(netAmountExpression.sum().coalesce(BigDecimal.ZERO))
			.from(user)
			.leftJoin(order).on(
				order.user.eq(user)
					.and(order.createdAt.after(threeMonthsAgo))
					.and(order.deletedAt.isNull())
			)
			.leftJoin(payment).on(payment.order.id.eq(order.id))
			.where(user.id.eq(userId))
			.fetchOne();

		Tuple levelInfo = queryFactory
			.select(membershipLevel.id, membershipLevel.name)
			.from(membershipLevel)
			.where(
				membershipLevel.minNetSpent.loe(netAmount),
				membershipLevel.maxNetSpent.goe(netAmount),
				membershipLevel.deletedAt.isNull()
			)
			.fetchFirst();

		Long membershipLevelId = levelInfo != null ? levelInfo.get(membershipLevel.id) : null;
		String membershipLevelName = levelInfo != null ? levelInfo.get(membershipLevel.name) : "등급 없음";

		return new UserMembershipLevelResponse(
			userId,
			netAmount,
			membershipLevelId,
			membershipLevelName
		);
	}

	@Override
	public void updateMembershipLevel(Long userId, Long newMembershipLevelId) {
		QUser user = QUser.user;

		queryFactory.update(user)
			.set(user.membershipLevel.id, newMembershipLevelId)
			.where(user.id.eq(userId))
			.execute();
	}

	@Override
	public Long findMembershipIdById(Long userId) {
		QUser user = QUser.user;

		return queryFactory
			.select(user.membershipLevel.id)
			.from(user)
			.where(user.id.eq(userId))
			.fetchOne();
	}

	private OrderSpecifier<?>[] getOrderSpecifier(Sort sort) {
		return sort.stream()
			.map(order -> {
				com.querydsl.core.types.Order direction = order.isAscending() ?
					com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
				String property = order.getProperty();

				if (property.equals("id")) {
					return new OrderSpecifier<>(direction, user.id);
				}
				return new OrderSpecifier<>(Order.DESC, user.id);
			}).toArray(OrderSpecifier[]::new);
	}

	private BooleanExpression searchCondition(String field, String keyword) {
		if (!StringUtils.hasText(keyword) || !StringUtils.hasText(field)) {
			return null;
		}

		return switch (field.toLowerCase()) {
			case "loginid" -> user.loginId.containsIgnoreCase(keyword);
			case "name" -> user.name.containsIgnoreCase(keyword);
			case "nickname" -> user.nickname.containsIgnoreCase(keyword);
			case "email" -> user.email.containsIgnoreCase(keyword);
			default -> null;
		};
	}

	private BooleanExpression userTypeFilter(String type) {
		if (!StringUtils.hasText(type)) {
			return null;
		}
		try {
			return user.type.eq(UserType.valueOf(type.toUpperCase()));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private BooleanExpression userStatusFilter(String status) {
		if (!StringUtils.hasText(status)) {
			return null;
		}
		try {
			return user.status.eq(UserStatus.valueOf(status.toUpperCase()));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private NumberExpression<BigDecimal> getNetAmountExpression(QOrder order, QPayment payment) {
		QBookOrder bookOrder = QBookOrder.bookOrder;
		QOrderPackaging orderPackaging = QOrderPackaging.orderPackaging;
		QPackagingOption packagingOption = QPackagingOption.packagingOption;

		NumberExpression<BigDecimal> wrappingPriceExpression =
			packagingOption.price.multiply(orderPackaging.quantity.castToNum(BigDecimal.class));

		SubQueryExpression<BigDecimal> wrappingCostSubquery = JPAExpressions
			.select(wrappingPriceExpression.sum())
			.from(bookOrder)
			.join(orderPackaging).on(orderPackaging.bookOrder.eq(bookOrder))
			.join(packagingOption).on(packagingOption.eq(orderPackaging.packagingOption))
			.where(bookOrder.order.eq(order))
			.groupBy(bookOrder.order.id);

		NumberExpression<BigDecimal> safeWrappingCost = Expressions.numberTemplate(
			BigDecimal.class, "coalesce({0}, {1})", wrappingCostSubquery, BigDecimal.ZERO
		);

		return new CaseBuilder()
			.when(payment.id.isNotNull())
			.then(order.originalAmount.subtract(safeWrappingCost))
			.otherwise(BigDecimal.ZERO);
	}
}