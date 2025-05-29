package shop.bluebooktle.backend.user.repository.impl;

import static shop.bluebooktle.common.entity.auth.QUser.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.repository.UserQueryRepository;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
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
}