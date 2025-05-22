package shop.bluebooktle.backend.user.repository.impl;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.user.repository.UserQueryRepository;
import shop.bluebooktle.common.entity.auth.QUser;
import shop.bluebooktle.common.entity.auth.User;

@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<User> findByBirthdayMonth(String birth) {
		QUser user = QUser.user;

		return queryFactory
			.selectFrom(user)
			.where(user.birth.substring(5, 7).eq(birth))
			.fetch();
	}
}
