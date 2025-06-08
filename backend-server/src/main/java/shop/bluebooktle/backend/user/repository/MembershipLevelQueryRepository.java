package shop.bluebooktle.backend.user.repository;

import java.math.BigDecimal;

import shop.bluebooktle.common.entity.auth.MembershipLevel;

public interface MembershipLevelQueryRepository {
	MembershipLevel findByMembershipLevelForAmount(BigDecimal netSpentAmount);
}
