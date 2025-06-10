package shop.bluebooktle.backend.user.repository.impl;

import java.math.BigDecimal;
import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.repository.MembershipLevelQueryRepository;
import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.QMembershipLevel;

@Slf4j
@RequiredArgsConstructor
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

	@Override
	public List<MembershipLevelDetailDto> findAllMembershipLevels() {
		QMembershipLevel membershipLevel = QMembershipLevel.membershipLevel;

		return queryFactory
			.select(Projections.constructor(MembershipLevelDetailDto.class,
				membershipLevel.id, membershipLevel.name, membershipLevel.rate, membershipLevel.minNetSpent,
				membershipLevel.maxNetSpent))
			.from(membershipLevel)
			.orderBy(membershipLevel.minNetSpent.asc())
			.fetch();
	}
}
