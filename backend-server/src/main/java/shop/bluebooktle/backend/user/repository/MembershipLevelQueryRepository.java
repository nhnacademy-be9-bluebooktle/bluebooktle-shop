package shop.bluebooktle.backend.user.repository;

import java.math.BigDecimal;
import java.util.List;

import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;
import shop.bluebooktle.common.entity.auth.MembershipLevel;

public interface MembershipLevelQueryRepository {
	MembershipLevel findByMembershipLevelForAmount(BigDecimal netSpentAmount);

	List<MembershipLevelDetailDto> findAllMembershipLevels();
}
