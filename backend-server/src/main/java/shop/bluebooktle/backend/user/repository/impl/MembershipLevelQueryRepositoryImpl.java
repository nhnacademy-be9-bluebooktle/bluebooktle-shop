package shop.bluebooktle.backend.user.repository.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.repository.MembershipLevelQueryRepository;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.QMembershipLevel;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MembershipLevelQueryRepositoryImpl implements MembershipLevelQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public MembershipLevel findByMembershipLevelForAmount(BigDecimal netSpentAmount) {

		QMembershipLevel membershipLevel = QMembershipLevel.membershipLevel;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(membershipLevel.minNetSpent.loe(netSpentAmount)
			.and(membershipLevel.maxNetSpent.goe(netSpentAmount)
				.and(membershipLevel.deletedAt.isNull())));

		return queryFactory
			.selectFrom(membershipLevel)
			.where(builder)
			.fetchFirst();
	}
}
