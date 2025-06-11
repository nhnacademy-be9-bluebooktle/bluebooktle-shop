package shop.bluebooktle.backend.user.service;

import java.util.List;

import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;

public interface MembershipLevelService {
	List<MembershipLevelDetailDto> getAllMembershipLevels();
}
