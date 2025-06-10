package shop.bluebooktle.backend.user.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.service.MembershipLevelService;
import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipLevelServiceImpl implements MembershipLevelService {

	private final MembershipLevelRepository membershipLevelRepository;

	@Override
	public List<MembershipLevelDetailDto> getAllMembershipLevels() {
		return membershipLevelRepository.findAllMembershipLevels();
	}
}
