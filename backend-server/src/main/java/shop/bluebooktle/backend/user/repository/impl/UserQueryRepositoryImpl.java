package shop.bluebooktle.backend.user.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.repository.UserQueryRepository;
import shop.bluebooktle.common.domain.auth.UserStatus;
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
}